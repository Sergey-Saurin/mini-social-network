package network.service;

import network.api.response.CommonListResponse;
import network.api.response.CommonResponseData;
import network.api.response.TagResponse;

/**
 * Created by Artem on 21.04.2021.
 */
public interface TagService {
  CommonListResponse getTags(String tag, long offset, int limit);

  CommonResponseData createTag(TagResponse tag);

  CommonResponseData deleteTag(long idTag);
}
