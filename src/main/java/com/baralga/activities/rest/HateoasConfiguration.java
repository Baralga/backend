package com.baralga.activities.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.DefaultLinkRelationProvider;
import org.springframework.util.StringUtils;

@Configuration
public class HateoasConfiguration {

    public class CustomLinkRelationProvider extends DefaultLinkRelationProvider {

        @Override
        public LinkRelation getCollectionResourceRelFor(Class<?> type) {
            var name = StringUtils.uncapitalize(type.getSimpleName().replace("Representation", ""));

            if (name.endsWith("y")) {
                return LinkRelation.of(name.replace("y", "ies"));
            }

            return LinkRelation.of(name + "s");
        }

    }

    @Bean
    public LinkRelationProvider provider() {
        return new CustomLinkRelationProvider();
    }
}
