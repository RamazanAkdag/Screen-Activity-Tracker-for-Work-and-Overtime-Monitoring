package com.id3.screentracker.startup;

import com.id3.screentracker.listener.IListenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final IListenerService listenerService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        listenerService.startListening();
    }
}
