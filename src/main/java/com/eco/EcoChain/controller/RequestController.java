package com.eco.EcoChain.controller;

import com.eco.EcoChain.dto.RequestRequestDto;
import com.eco.EcoChain.entity.Request;
import com.eco.EcoChain.entity.User;
import com.eco.EcoChain.service.objects.ChatService;
import com.eco.EcoChain.service.objects.RequestService;
import com.eco.EcoChain.service.objects.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/request")
public class RequestController {
    @Autowired
    private UserService userService;
    @Autowired
    private RequestService requestService;

    @ModelAttribute("currentUsername")
    public String getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userDetails.getUsername();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Request> patchRequest(@PathVariable Long requestId,
                                            @RequestBody RequestRequestDto requestDto,
                                                @ModelAttribute("currentUsername") String username) {
        return ResponseEntity.ok(requestService.updateRequest(requestId, requestDto, username));
    }

    @GetMapping("/my")
    public ResponseEntity<?> myRequest(@ModelAttribute("currentUsername") String username){
        return ResponseEntity.ok(requestService.getAllMyRequests(username));
    }

    @DeleteMapping("/{id}")
    public void deleteRequest(@PathVariable Long id, @ModelAttribute("currentUsername") String username){
        requestService.deleteRequest(id, username);
    }

    @GetMapping("/received")
    public ResponseEntity<?> receivedRequest(@ModelAttribute("currentUsername") String username){
        return ResponseEntity.ok(requestService.getAllRequestsReceived(username));
    }

    @PostMapping("/{materialId}")
    public ResponseEntity<?> request(@PathVariable Long materialId,
                                     @RequestBody RequestRequestDto requestDto,
                                     @ModelAttribute("currentUsername") String username){
        requestService.createRequest(materialId, username, requestDto.getMessage());
        return ResponseEntity.ok(Map.of("message", "Request sent successfully"));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id,
                                            @ModelAttribute("currentUsername") String username) {
        User user = userService.loadUserByUsername(username);

        requestService.checkRequestOwnership(id, username);
        requestService.approveRequest(id, user);

        return ResponseEntity.ok("Request approved.");
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<?> declineRequest(@PathVariable Long id, @ModelAttribute("currentUsername") String username) {
        requestService.checkRequestOwnership(id, username);

        requestService.declineRequest(id);
        return ResponseEntity.ok("Request declined.");
    }
}
