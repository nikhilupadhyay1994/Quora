package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.Answer;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
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

public class AnswerController {

    @Autowired
    private AnswerService answerService;
    private QuestionBusinessService questionBusinessService;
    private AuthenticationService authenticationService;


    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(@RequestHeader("authorization") final String authorization, @PathVariable("answerId") final String answeruuid, @RequestBody AnswerEditRequest answerEditRequest) throws AuthorizationFailedException, UserNotFoundException, AnswerNotFoundException {

        AnswerEditResponse answerEditResponse= new AnswerEditResponse().id( answerService.editAnswerContent(answeruuid,answerEditRequest.getContent(),authorization)).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDetailsResponse> getAnswers(@RequestHeader("authorization") final String authorization, @PathVariable("questionId") final String questionId) throws AuthorizationFailedException, InvalidQuestionException {

        List<Answer> answers=answerService.getAnswersForQuestionId(questionId, authorization);
        String uuid=questionId;
        String answerContent="";
        String questionContent="";


        for (Answer answer:answers) {
            answerContent+=answerContent+answer.getAnswer();
            questionContent=answer.getQuestion().getContent();
        }
        AnswerDetailsResponse answerDetailsResponse= new AnswerDetailsResponse();
        answerDetailsResponse.setId(uuid);
        answerDetailsResponse.setQuestionContent(questionContent);
        answerDetailsResponse.setAnswerContent(answerContent);


        return new ResponseEntity<AnswerDetailsResponse>(answerDetailsResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path="/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@RequestHeader("authorization") final String authorizationToken, @PathVariable("answerId") final String answerId) throws UserNotFoundException, AuthorizationFailedException, AnswerNotFoundException {
        AnswerDeleteResponse answerDeleteResponse = null;

        Answer answer = answerService.deleteAnswer(answerId, authorizationToken);
        answerDeleteResponse = new AnswerDeleteResponse().id(answer.getUuid()).status("ANSWER DELETED");

        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse,HttpStatus.OK);
    }

}
