package com.eco.EcoChain.service.objects;

import com.eco.EcoChain.dto.RequestRequestDto;
import com.eco.EcoChain.entity.Chat;
import com.eco.EcoChain.entity.Material;
import com.eco.EcoChain.entity.Request;
import com.eco.EcoChain.entity.User;
import com.eco.EcoChain.enums.RequestStatus;
import com.eco.EcoChain.repository.MaterialRepository;
import com.eco.EcoChain.repository.RequestRepository;
import lombok.SneakyThrows;
import org.aspectj.weaver.loadtime.definition.Definition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {
    @Autowired
    private UserService userService;
    @Autowired
    private MaterialService materialService;
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private DealService dealService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private RequestRepository requestRepository;

    public User getRequesterById(Long requestId){
        return requestRepository.findById(requestId).orElseThrow().getRequester();
    }

    public void createRequest(Long materialId, String username, String message){
        Request request = Request.builder()
                .material(materialService.getMaterialById(materialId))
                .requester(userService.loadUserByUsername(username))
                .message(message)
                .build();

        requestRepository.save(request);
    }

    @SneakyThrows
    public void checkRequestRequester(Request request, String username){
        if(!request.getRequester().getUsername().equals(username)){
            throw new AccessDeniedException("You are not the sender");
        }
    }

    @SneakyThrows
    public void checkRequestOwnership(Long requestId, String username) {
        if (!getRequesterById(requestId).getUsername().equals(username)) {
            throw new AccessDeniedException("Access denied");
        }
    }

    public void approveRequest(Long id, User supplier) {
        Request request = requestRepository.findById(id).orElseThrow();
        request.setStatus(RequestStatus.APPROVED);

        Material material = request.getMaterial();
        User consumer = request.getRequester();

        // Создаем чат
        Chat chat = chatService.save(supplier, consumer);
        // Создаем сделку
        dealService.createNewDeal(material, consumer, supplier, chat);
        requestRepository.save(request);
    }

    public void declineRequest(Long id) {
        Request request = requestRepository.findById(id).orElseThrow();
        request.setStatus(RequestStatus.DECLINED);
        requestRepository.save(request);
    }

    public List<Request> getAllMyRequests(String username){
        User requester = userService.loadUserByUsername(username);
        return requestRepository.findAllByRequester(requester);
    }

    public List<Request> getAllRequestsReceived(String username) {
        User user = userService.loadUserByUsername(username);
        return requestRepository.findByMaterialIn(materialRepository.findBySupplier(user));
    }

    public Request updateRequest(Long requestId, RequestRequestDto requestDto, String username) {
        Request request = requestRepository.findById(requestId).orElseThrow();
        checkRequestOwnership(requestId, username);
        Optional.ofNullable(requestDto.getMessage()).ifPresent(request::setMessage);
        return request;
    }

    public void deleteRequest(Long id, String username) {
        Request request = requestRepository.findById(id).orElseThrow();
        checkRequestRequester(request, username);
        requestRepository.delete(request);
    }
}
