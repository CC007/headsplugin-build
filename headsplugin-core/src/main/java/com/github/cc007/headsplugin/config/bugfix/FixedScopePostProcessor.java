package com.github.cc007.headsplugin.config.bugfix;

import dev.alangomes.springspigot.scope.SenderContextScope;
import lombok.val;
import org.bukkit.event.Listener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Arrays;

class FixedScopePostProcessor implements BeanFactoryPostProcessor {

    private SenderContextScope senderContextScope;

    FixedScopePostProcessor(SenderContextScope senderContextScope) {
        this.senderContextScope = senderContextScope;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
        factory.registerScope("sender", senderContextScope);
        Arrays.stream(factory.getBeanDefinitionNames()).forEach(beanName -> {
            val beanDef = factory.getBeanDefinition(beanName);
            val beanType = factory.getType(beanName);
            if (beanType != null && beanType.isAssignableFrom(Listener.class)) {
                beanDef.setScope(SCOPE_SINGLETON);
            }
        });
    }

}