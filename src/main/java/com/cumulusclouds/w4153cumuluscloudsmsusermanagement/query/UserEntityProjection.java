package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.query;

import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.api.ShortUser;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.api.UserCreatedEvent;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.api.UsersNamedQueries;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

@Component
public class UserEntityProjection {

    private final UserEntityRepository userEntityRepository;
    private final QueryUpdateEmitter updateEmitter;

    public UserEntityProjection(UserEntityRepository userEntityRepository, QueryUpdateEmitter updateEmitter) {
        this.userEntityRepository = userEntityRepository;
        this.updateEmitter = updateEmitter;
    }

    @EventHandler
    public void on(UserCreatedEvent event) {
        var shortUser = new ShortUser(event.userID(), event.username(), event.email()); //<.>
        userEntityRepository.save(shortUser);
        updateEmitter.emit(q -> "findAll".equals(q.getQueryName()), shortUser);

    }

    @QueryHandler(queryName = UsersNamedQueries.FIND_ALL) //<.>
    public Iterable<ShortUser> findAll() { // <.>
        return userEntityRepository.findAll(); //<.>
    }
}
