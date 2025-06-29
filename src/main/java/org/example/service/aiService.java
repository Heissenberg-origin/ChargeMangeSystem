package org.example.service;

import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class aiService {
    @Resource
    private SparkClient sparkClient;
    public static final String PRECONDITION = "在这里填写对Spark的预设：\n" +
            "“你是医院的前台”\n" +
            "给我一些关于医院挂号与收费方面的指导\n" +
            "回答方向为医疗方向";

    public String sendHttpTOSpark(final String content) {
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.systemContent(PRECONDITION));
        messages.add(SparkMessage.userContent(content));
        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 指定请求版本，lite版本是v1.5
                .apiVersion(SparkApiVersion.V4_0)
                .build();
        // 同步调用
        SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
        String responseContent = chatResponse.getContent();
        System.out.println("Spark AI 返回的结果：" + responseContent);
        return responseContent;
    }
}
