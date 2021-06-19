package network.service.impl;

import lombok.RequiredArgsConstructor;
import network.api.response.CommonListResponse;
import network.model.entity.Post;
import network.repository.util.Utils;
import network.service.AccountService;
import network.service.FeedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import network.api.response.PostResponse;
import network.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static network.enums.FriendshipStatusCode.*;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final PostRepository postRepository;
    private final AccountService accountService;


    @Override
    public CommonListResponse getNewsList(String name, long offset, int itemPerPage) {
        long personId = accountService.getCurrentPerson().getId();
        Pageable page = Utils.getPageable(offset, itemPerPage, Sort.by(Sort.Direction.DESC, "time"));
        Page<Post> posts = postRepository.findAllNews(personId, FRIEND.name(), page);

        return new CommonListResponse("", LocalDateTime.now(), posts.getTotalElements(),
                offset, itemPerPage, new ArrayList<>(PostResponse.fromPostList(posts.getContent())));
    }
}
