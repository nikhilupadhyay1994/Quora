package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/*")
public class QuestionController {

    @Autowired
    QuestionBusinessService questionBusinessService;
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        questionBusinessService.deleteQuestion(questionId, authorization);
        QuestionDeleteResponse qsnDeleteResponse = new QuestionDeleteResponse().id(questionId).status("DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(qsnDeleteResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody List<QuestionDetailsResponse> getAllQuestion(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        List<QuestionDetailsResponse> deleteResponses= new ArrayList<>() ;
        List<Question> questions= questionBusinessService.getAllQuestion(authorization);
        for(Question q: questions)
        {
            deleteResponses.add(new QuestionDetailsResponse().id(q.getUuid()).content(q.getContent()));
        }


        //return new ResponseEntity<List<QuestionDetailsResponse>>( questions, HttpStatus.OK);
        return deleteResponses;
    }


    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody List<QuestionDetailsResponse> getAllQuestionByUser(@RequestHeader("authorization") final String authorization,  @PathVariable("userId") final String userUuid) throws AuthorizationFailedException, UserNotFoundException {
        List<QuestionDetailsResponse> deleteResponses= new ArrayList<>() ;
        List<Question> questions= questionBusinessService.getAllQuestionByUser(authorization,userUuid);
        for(Question q: questions)
        {
            deleteResponses.add(new QuestionDetailsResponse().id(q.getUuid()).content(q.getContent()));
        }
        return deleteResponses;
    }
}
