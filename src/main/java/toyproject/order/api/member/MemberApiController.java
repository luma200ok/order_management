package toyproject.order.api.member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toyproject.order.api.member.dto.CreateMemberRequest;
import toyproject.order.api.member.dto.CreateMemberResponse;
import toyproject.order.api.member.dto.MemberResponse;

import toyproject.order.service.MemberService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberService memberService;


    @PostMapping
    public ResponseEntity<CreateMemberResponse> create(@RequestBody @Valid CreateMemberRequest request) {
        Long id = memberService.join(request.name());
//        return ResponseEntity.ok(new CreateMemberResponse(id));
        return ResponseEntity.status(201).body(new CreateMemberResponse(id));
    }

    @GetMapping
    public List<MemberResponse> list() {
        return memberService.findMembers().stream().map(
                m -> new MemberResponse(m.getId(), m.getName()))
                .toList();
    }
}
