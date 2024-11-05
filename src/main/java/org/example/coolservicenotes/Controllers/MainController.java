package org.example.coolservicenotes.Controllers;

import org.example.coolservicenotes.Entity.Message;
import org.example.coolservicenotes.Entity.User;
import org.example.coolservicenotes.reposoritory.MessagRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ControllerUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
public class MainController {
    @Autowired
    private MessagRepo messagRepo;

    @Value("${upload.path}")
    private String uploadPath;
@GetMapping("/")
public String greeting(Map<String, Object> model) {
    return "greeting";
}

@GetMapping("/main")
public String main(@RequestParam(required = false, defaultValue = "") String filter, Model model) {
    Iterable<Message> messages;

    if (filter != null && !filter.isEmpty()) {
        messages = messagRepo.findByTag(filter);
    } else {
        messages = messagRepo.findAll();
    }

    model.addAttribute("messages", messages);
    model.addAttribute("filter", filter);

    return "main";
}

@PostMapping("/main")
public String add(
        @AuthenticationPrincipal User user,
        @Valid Message message,
        BindingResult bindingResult,
        Model model,
        @RequestParam("file") MultipartFile file
) throws IOException {
    message.setAuthor(user);

    if (bindingResult.hasErrors()) {
        Map<String, String> errors = ControllerUtils.getErrors(bindingResult);

        model.mergeAttributes(errors);
        model.addAttribute("message", message);
    } else {
        saveFile(message, file);

        model.addAttribute("message", null);

        messagRepo.save(message);
    }

    Iterable<Message> messages = messagRepo.findAll();

    model.addAttribute("messages", messages);

    return "main";
}

private void saveFile(@Valid Message message, @RequestParam("file") MultipartFile file) throws IOException {
    if (file != null && !file.getOriginalFilename().isEmpty()) {
        File uploadDir = new File(uploadPath);

        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        String uuidFile = UUID.randomUUID().toString();
        String resultFilename = uuidFile + "." + file.getOriginalFilename();

        file.transferTo(new File(uploadPath + "/" + resultFilename));

        message.setFilename(resultFilename);
    }
}

@GetMapping("/user-messages/{user}")
public String userMessges(
        @AuthenticationPrincipal User currentUser,
        @PathVariable User user,
        Model model,
        @RequestParam(required = false) Message message
) {
    Set<Message> messages = user.getMessages();

    model.addAttribute("messages", messages);
    model.addAttribute("message", message);
    model.addAttribute("isCurrentUser", currentUser.equals(user));

    return "userMessages";
}

@PostMapping("/user-messages/{user}")
public String updateMessage(
        @AuthenticationPrincipal User currentUser,
        @PathVariable Long user,
        @RequestParam("id") Message message,
        @RequestParam("text") String text,
        @RequestParam("tag") String tag,
        @RequestParam("file") MultipartFile file
) throws IOException {
    if (message.getAuthor().equals(currentUser)) {
        if (!StringUtils.isEmpty(text)) {
            message.setText(text);
        }

        if (!StringUtils.isEmpty(tag)) {
            message.setTag(tag);
        }

        saveFile(message, file);

        messagRepo.save(message);
    }

    return "redirect:/user-messages/" + user;
}}