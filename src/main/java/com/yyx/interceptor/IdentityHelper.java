package com.yyx.interceptor;

import com.alibaba.fastjson.JSONObject;
import lombok.NonNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

final class IdentityHelper {

    static boolean checkIdentity(HttpServletRequest request, HttpServletResponse response,
                                 @NonNull String expectedRole) throws Exception {
        HttpSession session = request.getSession(false);
        if(session != null) {
            String actualRole = session.getAttribute("role").toString();
            if(expectedRole.equals(actualRole)) {
                return true;
            }
        }
        PrintWriter writer = response.getWriter();
        writer.print(new JSONObject(1).fluentPut("AccessDenied", true).toJSONString());
        writer.flush();
        writer.close();
        return false;
    }

}
