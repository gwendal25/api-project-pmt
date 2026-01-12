package com.iscod.api_project_pmt.services;

import java.util.List;

public interface EmailService {
    void SendSimpleMessage(String to, String subject, String text);

    void SendProjectInvite(String from, String to, String project, String role);

    void SendTaskAssignNotification(String to, String project, String task, String assignedTo);

    void SendTaskAssignNotificationBulk(List<String> tos, String project, String task, String assignedTo);
}
