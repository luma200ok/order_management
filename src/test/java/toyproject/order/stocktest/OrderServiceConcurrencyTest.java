package toyproject.order.stocktest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;
import toyproject.order.domain.Item;
import toyproject.order.domain.Member;
import toyproject.order.repository.ItemRepository;
import toyproject.order.repository.MemberRepository;
import toyproject.order.service.OrderService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class OrderServiceConcurrencyTest {

    @Autowired OrderService orderService;
    @Autowired ItemRepository itemRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired TransactionTemplate tx;


    /**
     * 주문 로직에서 재고 차감이 트랜잭션 안에서 일어난다
     *
     * 동시 주문 시에 락이 걸리거나(비관적 락)
     * 혹은 어떤 방식으로든 동시 업데이트 충돌이 제어된다
     *
     * 결과로 재고가 0으로 수렴하고 음수로 가지 않는다
     */

    @Test
    void 주문_음수_안됨() throws Exception {
        // 1) 데이터 준비
        TestIds ids = tx.execute(status ->
        {
            Member m = new Member("user1");
            memberRepository.save(m);

            Item i = new Item("item", 1000, 1);
            itemRepository.save(i);

            return new TestIds(m.getId(), i.getId());
        });

        Long memberId = ids.memberId;
        Long itemId = ids.itemId;

        // 2)멀티 스레드 주문
        int threadCount =2;
        ExecutorService pool = Executors.newFixedThreadPool(2);

        // 스레드 동시 출발 래치
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);


        for (int i = 0; i <threadCount; i++) {
            pool.submit(() -> {
                try {
                    startLatch.await(); // 출발 대기
                    orderService.order(memberId, itemId, 1);
                    success.incrementAndGet();
                } catch (IllegalStateException e) {
                    // 실패 정상
                    System.out.println("에러 발생 이유: " + e.getMessage());
                    fail.incrementAndGet();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 동시 출발
        startLatch.countDown();
        doneLatch.await();
        pool.shutdown();

        // 결과 검증 (새 트렌잭션에서 DB 기준 조회)
        Integer stock = tx.execute(status
                -> itemRepository.findOne(itemId).getStockQuantity());

        assertThat(success.get()).isEqualTo(1);
        assertThat(fail.get()).isEqualTo(1);
        assertThat(stock).isEqualTo(0);

    }

    static class TestIds {
        final Long memberId;
        final Long itemId;
        TestIds(Long memberId, Long itemId) {
            this.memberId = memberId;
            this.itemId = itemId;
        }
    }
}
