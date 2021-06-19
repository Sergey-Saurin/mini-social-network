package network.service;

import network.api.response.CommonListResponse;

/**
 * Created by User on 10.06.2021.
 */
public interface FeedService {

    CommonListResponse getNewsList (String name, long offset, int itemPerPage);
}
