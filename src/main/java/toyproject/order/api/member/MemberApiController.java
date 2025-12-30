package toyproject.order.api.member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toyproject.order.api.member.dto.CreateMemberRequest;
import toyproject.order.api.member.dto.CreateMemberResponse;
import toyproject.order.api.member.dto.MemberResponse;
import toyproject.order.domain.Member;
import toyproject.order.repository.MemberRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberRepository memberRepository;

    @PostMapping
    public ResponseEntity<CreateMemberResponse> create(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member(request.name());
        memberRepository.save(member);
        return ResponseEntity.ok(new CreateMemberResponse(member.getId()));
    }

    @GetMapping
    public List<MemberResponse> list() {
        return memberRepository.findAll().stream().map(
                m -> new MemberResponse(m.getId(), m.getName()))
                .toList();
    }
}
