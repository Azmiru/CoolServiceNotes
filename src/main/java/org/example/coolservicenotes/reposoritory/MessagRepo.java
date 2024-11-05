package org.example.coolservicenotes.reposoritory;

import org.example.coolservicenotes.Entity.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessagRepo extends CrudRepository<Message, Long> {
    List<Message> findBySender(String sender);

    Iterable<Message> findByTag(String filter);
}
