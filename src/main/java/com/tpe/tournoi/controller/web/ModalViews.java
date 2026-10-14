package com.tpe.tournoi.controller.web;

import jakarta.servlet.http.HttpServletRequest;

public final class ModalViews {

    private ModalViews() {
    }

    public static boolean isModal(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
                || "1".equals(request.getParameter("modal"));
    }

    public static String of(String viewName, HttpServletRequest request) {
        return isModal(request) ? viewName + " :: modalContent" : viewName;
    }
}
