package id.co.bcaf.solvr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ControllerListController {

    @Autowired
    private ApplicationContext applicationContext;

//    @GetMapping("/controllers")
//    public List<String> getAllControllers() {
//        Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(Controller.class);
//        Map<String, Object> restControllers = applicationContext.getBeansWithAnnotation(RestController.class);
//
//        List<String> allControllers = new ArrayList<>();
//        controllers.keySet().forEach(allControllers::add);
//        restControllers.keySet().forEach(allControllers::add);
//
//        return allControllers;
//    }


    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @GetMapping("/controllers")
    public List<String> getAllControllers() {
        return handlerMapping.getHandlerMethods().keySet()
                .stream()
                .map(key -> key.toString())
                .collect(Collectors.toList());
    }


    @GetMapping("/endpoints")
    public Map<String, String> getAllEndpoints() {
        Map<String, String> endpoints = new HashMap<>();

        handlerMapping.getHandlerMethods().forEach((key, value) -> {
            String pattern = key.getPatternsCondition().getPatterns().toString();
            String method = key.getMethodsCondition().getMethods().toString();
            String controller = value.getBeanType().getSimpleName();

            endpoints.put(pattern + " " + method, controller + "." + value.getMethod().getName());
        });

        return endpoints;
    }
}
