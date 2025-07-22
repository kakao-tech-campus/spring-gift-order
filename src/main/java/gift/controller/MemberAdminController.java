package gift.controller;

import gift.dto.MemberRequest;
import gift.dto.MemberResponse;
import gift.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/members")
public class MemberAdminController {

    private final MemberService memberService;

    public MemberAdminController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public String showMemberList(Model model) {
        List<MemberResponse> members = memberService.findAllMembers();
        model.addAttribute("members", members);
        return "admin/member-list";
    }

    @GetMapping("/add")
    public String showMemberAddForm(Model model) {
        model.addAttribute("memberRequest", new MemberRequest("", ""));
        return "admin/member-form";
    }

    @PostMapping("/add")
    public String addMember(@ModelAttribute MemberRequest memberRequest) {
        memberService.register(memberRequest);
        return "redirect:/admin/members";
    }

    @GetMapping("/edit/{id}")
    public String showMemberEditForm(@PathVariable("id") Long id, Model model) {
        MemberResponse member = memberService.findMemberById(id);
        model.addAttribute("memberRequest", new MemberRequest(member.email(), ""));
        model.addAttribute("memberId", id);
        return "admin/member-edit-form";
    }

    @PostMapping("/edit/{id}")
    public String editMember(@PathVariable("id") Long id, @ModelAttribute MemberRequest memberRequest) {
        memberService.updateMember(id, memberRequest);
        return "redirect:/admin/members";
    }

    @PostMapping("/delete/{id}")
    public String deleteMember(@PathVariable("id") Long id) {
        memberService.deleteMember(id);
        return "redirect:/admin/members";
    }
}