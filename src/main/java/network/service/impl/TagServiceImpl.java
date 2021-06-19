package network.service.impl;

import lombok.RequiredArgsConstructor;
import network.api.response.CommonListResponse;
import network.api.response.CommonResponseData;
import network.api.response.StatusMessageResponse;
import network.model.entity.Tag;
import network.repository.util.Utils;
import network.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import network.api.response.TagResponse;
import network.repository.TagRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Artem on 21.04.2021.
 */

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Override
    public CommonListResponse getTags(String tag, long offset, int limit) {
        Page<Tag> tagPage = tagRepository.findAllByTag(
            tag, Utils.getPageable(offset, limit, Sort.by(Sort.DEFAULT_DIRECTION, "tag"))
        );
        return new CommonListResponse(
            "", LocalDateTime.now(),
            tagPage.getTotalElements(), offset, limit,
            tagPage.getContent().stream().map(t -> new TagResponse(t.getId(), t.getTag())).collect(Collectors.toList())
        );
    }

    @Override
    public CommonResponseData createTag(TagResponse tagNew) {
        Tag tag = tagRepository.findByTag(tagNew.getTag());
        if (tag != null) {
            return new CommonResponseData(null, "tag already exists ");
        }
        Tag tagModel = new Tag(tagNew.getTag());
        Tag newTagModel = tagRepository.save(tagModel);

        return new CommonResponseData(new TagResponse(newTagModel.getId(), newTagModel.getTag()), "");
    }

    @Override
    public CommonResponseData deleteTag(long idTag) {
        Optional<Tag> optionalTag = tagRepository.findById(idTag);
        if (optionalTag.isEmpty()) {
            return new CommonResponseData(null, idTag + " not found");
        }
        Tag tag = optionalTag.get();
        tagRepository.delete(tag);

        return  new CommonResponseData(new StatusMessageResponse("ok"), "");
    }
}
