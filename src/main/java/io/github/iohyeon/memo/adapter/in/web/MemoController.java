package io.github.iohyeon.memo.adapter.in.web;

import io.github.iohyeon.memo.application.MemoService;
import io.github.iohyeon.memo.domain.Visibility;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/memos")
class MemoController {

    record CreateMemoRequest(String ownerId, String title, Visibility visibility) {
    }

    record CreateMemoResponse(long id) {
    }

    private final MemoService memoService;

    MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CreateMemoResponse create(@RequestBody CreateMemoRequest request) {
        long id = memoService.register(request.ownerId(), request.title(), request.visibility());
        return new CreateMemoResponse(id);
    }

    @PatchMapping("/{id}/archive")
    void archive(@PathVariable long id) {
        memoService.archive(id);
    }
}
