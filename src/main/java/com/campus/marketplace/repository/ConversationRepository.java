package com.campus.marketplace.repository;

import com.campus.marketplace.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends MongoRepository<Conversation, String> {

    @Query("{ 'participantIds': { $all: [?0, ?1] }, 'listingId': ?2 }")
    Optional<Conversation> findByBothParticipantsAndListing(String userId1, String userId2, String listingId);

    @Query("{ 'participantIds': { $all: [?0, ?1] }, 'listingId': null }")
    Optional<Conversation> findDirectConversation(String userId1, String userId2);

    List<Conversation> findByParticipantIdsContainingOrderByLastMessageAtDesc(String userId);
}
