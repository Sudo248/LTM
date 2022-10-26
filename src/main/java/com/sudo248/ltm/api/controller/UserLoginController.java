package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entities.UserEntity;
import com.sudo248.ltm.api.model.auth.Account;
import com.sudo248.ltm.api.security.payload.LoginRequest;
import com.sudo248.ltm.api.security.payload.Status;
import com.sudo248.ltm.api.service.LoginService;
import com.sudo248.ltm.api.service.UserService;
import com.sudo248.ltm.websocket.WebSocketServerImpl;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.annotation.WsPost;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@WsController (path = "/user/login")
public class UserLoginController implements WebSocketController<Request<Account>, Response<Account>> {

    private final Logger logger = LoggerFactory.getLogger(UserLoginController.class);

    @Autowired
    LoginService loginService;

    @Override
    public void onGet(Request<Account> request, Response<Account> response) {

    }

    @Override
    public void onPost(Request<Account> request, Response<Account> response) {
        String message;
        Integer userId;
        Account account = request.getPayload();

        try {
            LoginRequest loginRequest = new LoginRequest(
                    request.getPayload().getEmail(),
                    request.getPayload().getPassword()
            );
            if ((userId = loginService.checkAccount(loginRequest)) != null) {
                message = "Đăng nhập thành công.";
                account.setId(userId.longValue());
                response.setCode(200);
            } else {
                message = "Mật khẩu chưa chính xác, vui lòng kiểm tra lại.";
                response.setCode(401);
            }
            response.setMessage(message);
            response.setPayload(account);
        } catch (Exception e) {
            logger.error(e.getMessage());
            message = loginService.checkLogin(account.getEmail());
            response.setCode(401);
            response.setMessage(message);
            response.setPayload(account);
        }
    }
}
