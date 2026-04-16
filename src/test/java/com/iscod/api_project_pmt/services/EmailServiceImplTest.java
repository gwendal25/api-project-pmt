package com.iscod.api_project_pmt.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private JavaMailSender emailSender;

    /**
     * Test case for the SendSimpleMessage method in EmailServiceImpl.
     * This test verifies that the method correctly sends an email with the specified parameters.
     */
    @Test
    public void testSendSimpleMessage_ValidInput_EmailSent() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "This is a test email.";

        emailService.SendSimpleMessage(to, subject, text);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(emailSender, Mockito.times(1)).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertArrayEquals(new String[]{to}, message.getTo());
        assertEquals(subject, message.getSubject());
        assertEquals(text, message.getText());
        assertEquals("gwendalbreton.apppmt@gmail.com", message.getFrom());
        assertNull(message.getCc());
        assertNull(message.getBcc());

    }

    /**
     * Test case for the SendSimpleMessage method in EmailServiceImpl.
     * This test ensures that the method does not throw any exception when called with a valid email.
     */
    @Test
    public void testSendSimpleMessage_ValidEmail_NoExceptions() {
        // Arrange
        String to = "valid.email@example.com";
        String subject = "Test Subject";
        String text = "This is another test email.";

        emailService.SendSimpleMessage(to, subject, text);

        Mockito.verify(emailSender, Mockito.times(1)).send(any(SimpleMailMessage.class));
    }

    /**
     * Test case for the SendSimpleMessage method in EmailServiceImpl.
     * This test handles a scenario where the recipient email is empty.
     */
    @Test
    public void testSendSimpleMessage_EmptyRecipient_DoNothing() {
        // Arrange
        String to = "";
        String subject = "Test Subject";
        String text = "This email should not be sent.";

        emailService.SendSimpleMessage(to, subject, text);

        Mockito.verify(emailSender, Mockito.never()).send(any(SimpleMailMessage.class));
    }

    /**
     * Test case for the SendProjectInvite method in EmailServiceImpl.
     * This test verifies that the method correctly constructs and sends an email for project invitation.
     */
    @Test
    public void testSendProjectInvite_ValidInput_EmailSent() {
        String from = "sender@example.com";
        String to = "recipient@example.com";
        String project = "Test Project";
        String role = "Developer";

        emailService.SendProjectInvite(from, to, project, role);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(emailSender, Mockito.times(1)).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertEquals("Invitation au project Test Project", message.getSubject());
        assertEquals("sender@example.com vous invite à rejoindre le projet Test Project en tant que Developer.\n Vous avez automatiquement été ajouter au projet.", message.getText());
        assertArrayEquals(new String[]{to}, message.getTo());
        assertEquals("gwendalbreton.apppmt@gmail.com", message.getFrom());
    }

    /**
     * Test case for the SendProjectInvite method in EmailServiceImpl.
     * This test ensures that no email is sent when the recipient is empty.
     */
    @Test
    public void testSendProjectInvite_EmptyRecipient_DoNothing() {
        String from = "sender@example.com";
        String to = "";
        String project = "Test Project";
        String role = "Developer";

        emailService.SendProjectInvite(from, to, project, role);

        Mockito.verify(emailSender, Mockito.never()).send(any(SimpleMailMessage.class));
    }
}