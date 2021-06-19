package network.controller;

import network.api.request.ProfileEditRequest;
import network.api.response.CommonListResponse;
import network.api.response.CommonResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import network.api.request.PostRequest;
import network.service.ProfileService;

@RestController
@RequestMapping("/users")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(
            ProfileService profileService
    ) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public CommonResponseData getCurrentUser() {
        return profileService.getCurrentUser();
    }

    @PutMapping("/me")
    public CommonResponseData editCurrentUser(@RequestBody ProfileEditRequest profileEditRequest) {
        return profileService.editCurrentUser(profileEditRequest);
    }

    @DeleteMapping("/me")
    public CommonResponseData deleteCurrentUser() {
        return profileService.deleteCurrentUser();
    }

    @GetMapping("/{id}")
    public CommonResponseData getUserById(@PathVariable("id") long id) {
        return profileService.findUserById(id);
    }

    @GetMapping("/{id}/wall")
    public CommonListResponse getPostsUserWall(
            @PathVariable("id") long id,
            @RequestParam(name = "offset", defaultValue = "0") long offset,
            @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage) {
        return profileService.getUserWall(id, offset, itemPerPage);
    }

    @PostMapping("/{id}/wall")
    public CommonResponseData addPostUserWall(
            @PathVariable("id") long id,
            @RequestBody PostRequest postRequestBody) {
        return profileService.postOnUserWall(id, postRequestBody);
    }

    @GetMapping("/search")
    public CommonListResponse searchUser(
            @RequestParam(name = "first_name", required = false) String firstName,
            @RequestParam(name = "last_name", required = false) String lastName,
            @RequestParam(name = "age_from", required = false) Integer ageFrom,
            @RequestParam(name = "age_to", required = false) Integer ageTo,
            @RequestParam(name = "country_id", required = false) String country,
            @RequestParam(name = "city_id", required = false) String city,
            @RequestParam(name = "offset", defaultValue = "0") long offset,
            @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage
    ) {
        return profileService
                .searchUser(firstName, lastName, ageFrom, ageTo, country, city, offset, itemPerPage);
    }

    @PutMapping("/block/{id}")
    public CommonResponseData blockUserById(@PathVariable("id") long id) {
        return profileService.blockUser(true, id);
    }

    @DeleteMapping("/block/{id}")
    public CommonResponseData unblockUserById(@PathVariable("id") long id) {
        return profileService.blockUser(false, id);
    }
}
