package toyproject.order.api.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toyproject.order.api.member.dto.CreateMemberRequest;
import toyproject.order.api.member.dto.CreateMemberResponse;
import toyproject.order.api.member.dto.MemberResponse;

import toyproject.order.service.MemberService;

import java.util.List;

@Tag(name = "Members", description = "회원 등록/조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberService memberService;

    @Operation(summary = "회원 등록",description = "등록된 회원 목록을 조회")
    @PostMapping
    public ResponseEntity<CreateMemberResponse> create(@RequestBody @Valid CreateMemberRequest request) {
        Long id = memberService.join(request.name());
//        return ResponseEntity.ok(new CreateMemberResponse(id));
        return ResponseEntity.status(201).body(new CreateMemberResponse(id));
    }

    @Operation(summary = "회원 목록 조회",description = "회원 이름을 입력받아 회원 등록.")
    @GetMapping
    public List<MemberResponse> list() {
        return memberService.findMembers().stream().map(
                        m -> new MemberResponse(m.getId(), m.getName()))
                .toList();
    }
}
