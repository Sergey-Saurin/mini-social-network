package network.service.impl;

import network.model.entity.Person;
import network.service.AccountService;
import network.service.FriendsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import network.model.entity.Friendship;
import network.model.entity.FriendshipStatus;
import network.model.entity.dto.FriendsIdDTO;
import network.repository.FriendsRepository;
import network.repository.PersonRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static network.enums.FriendshipStatusCode.*;

@Service
public class FriendsServiceImpl implements FriendsService {

    private final FriendsRepository friendsRepository;
    private final AccountService accountService;
    private final PersonRepository personRepository;

    public FriendsServiceImpl(FriendsRepository friendsRepository, AccountService accountService, PersonRepository personRepository) {
        this.friendsRepository = friendsRepository;
        this.accountService = accountService;
        this.personRepository = personRepository;
    }

    @Override
    public List<FriendsIdDTO> isFriends(List<Long> userIds) {
        long dstId = accountService.getCurrentPerson().getId();
        List<FriendsIdDTO> friends = friendsRepository.isFriends(dstId, userIds);
        return friends;
    }

    @Override
    public void deleteById(long srcId) {
        long dstId = accountService.getCurrentPerson().getId();
        Friendship friendship = friendsRepository
                .findAllBySrcPersonIdAndDstPersonId(srcId, dstId)
                .orElseThrow(NoSuchElementException::new);
        friendsRepository.delete(friendship);
    }

    @Override
    public void addFriend(long srcId) {
        Person dstPerson = accountService.getCurrentPerson();
        Person srcPerson = personRepository.findById(srcId);
        Friendship friendship = friendsRepository
                .findAllBySrcPersonIdAndDstPersonId(srcPerson.getId(), dstPerson.getId())
                .orElse(new Friendship());

        FriendshipStatus status;

        if (friendship.getStatus() == null) {
            status = new FriendshipStatus();
            status.setCode(REQUEST.name());
            status.setName(REQUEST.name());
            status.setTime(LocalDateTime.now());
            friendship.setStatus(status);
            friendship.setDstPerson(dstPerson);
            friendship.setSrcPerson(srcPerson);
        } else {
            status = friendship.getStatus();
            status.setCode(FRIEND.name());
            status.setName(FRIEND.name());
            status.setTime(LocalDateTime.now());
        }
        friendsRepository.save(friendship);
    }

    @Override
    public Page<Person> getFriends(String name, String code, Pageable pageable) {
        long id = accountService.getCurrentPerson().getId();
        return personRepository.findAllFriends(id, code, pageable);
    }

    @Override
    public Page<Person> getRecommendations(Pageable pageable) {
        long id = accountService.getCurrentPerson().getId();
        Page<Person> recommendations = personRepository.getRecommendations(id, pageable);
        return recommendations;
    }
}
