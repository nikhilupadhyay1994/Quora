package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.exception.AuthorizationFailedException;

import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/")
public class QuestionController {


    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.POST, path="/question/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest ,@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        Question questionEntity = new Question();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setTimeStamp(ZonedDateTime.now());

        final Question createdQuestionEntity;
        createdQuestionEntity = questionBusinessService.createQuestion(questionEntity, authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

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

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(@RequestHeader("authorization") final String authorization, @PathVariable("questionId")final String questionuuid, @RequestBody QuestionEditRequest questionEditRequest) throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEditResponse questionEditResponse= new QuestionEditResponse().id( questionBusinessService.editQuestionContent(questionuuid,questionEditRequest.getContent(),authorization)).status("Question EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }
}

