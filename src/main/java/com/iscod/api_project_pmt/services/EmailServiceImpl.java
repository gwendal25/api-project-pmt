package com.iscod.api_project_pmt.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

@Component
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void SendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("gwendalbreton.apppmt@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void SendProjectInvite(String from, String to, String project, String role) {
        String subject = MessageFormat.format("Invitation au project {0}", project);
        String text = MessageFormat.format("{0} vous invite à rejoindre le projet {1} en tant que {2}.\n Vous avez automatiquement été ajouter au projet.", from, project, role);
        SendSimpleMessage(to, subject, text);
    }

    @Override
    public void SendTaskAssignNotification(String to, String project, String task, String assignedTo) {
        String subject = MessageFormat.format("Tâche {0} assigné", task);
        String text = MessageFormat.format("La tâche {0} du projet {1} a été assigné à {2}.\n Vous recevez cette notification car vous avez activez les notifications par mail.", task, project, assignedTo);
        SendSimpleMessage(to, subject, text);
    }

    @Override
    public void SendTaskAssignNotificationBulk(List<String> tos, String project, String task, String assignedTo) {
        for(String to : tos) {
            SendTaskAssignNotification(to, project, task, assignedTo);
        }
    }
}
