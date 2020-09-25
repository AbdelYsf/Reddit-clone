package com.abedlysf.reddit.clone.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class MailContentBuilder {

    //public  TemplateEngine templateEngine;


    public String build(String message) {
        Context context = new Context();
        context.setVariable("message", message);
        return new TemplateEngine().process("mailTemplate", context);
    }
}
