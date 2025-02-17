package com.es.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.Date;

public class BulkDemo {
    private static Logger logger = LogManager.getRootLogger();

    public static void mockData(BulkRequest request) {
        request.add(new IndexRequest("fbp", "_doc", "4").source(XContentType.JSON,
                "country", "西班牙",
                "name", "哈维",
                "club", "巴塞罗那俱乐部"));
        request.add(new IndexRequest("fbp", "_doc", "5").source(XContentType.JSON,
                "country", "葡萄牙",
                "name", "c罗",
                "club", "皇家马德里俱乐部"));
        request.add(new IndexRequest("fbp", "_doc", "6").source(XContentType.JSON,
                "country", "德国",
                "name", "克洛泽",
                "club", "拜仁慕尼黑俱乐部"));

    }

    public static void main(String[] args) {
        try {
            RestHighLevelClient client = InitClient.getClient();
            // 1、创建批量操作请求参数
            BulkRequest request = new BulkRequest();
//            mockData(request);
            request.add(new IndexRequest("book13", "_doc", "1")
                    .source(XContentType.JSON,
                            "postDate", new Date(),
                            "user", "liming"));
            request.add(new UpdateRequest("book13", "_doc", "2")
                    .doc(XContentType.JSON, "user", "liming"));
            request.add(new IndexRequest("book13", "_doc", "3")
                    .source(XContentType.JSON, "message", "add a doc"));

            /*
            request.add(new DeleteRequest("mess", "_doc", "3"));
            request.add(new UpdateRequest("mess", "_doc", "2")
                    .doc(XContentType.JSON,"other", "test"));
            request.add(new IndexRequest("mess", "_doc", "4")
                    .source(XContentType.JSON,"field", "baz"));
            */

            // 2、可选的设置
            /*
            request.timeout("2m");
            request.setRefreshPolicy("wait_for");
            request.waitForActiveShards(2);
            */


            //3、发送请求

            // 同步请求
            BulkResponse bulkResponse = client.bulk(request);


            //4、处理响应
            if (bulkResponse != null) {
                for (BulkItemResponse bulkItemResponse : bulkResponse) {
                    DocWriteResponse itemResponse = bulkItemResponse.getResponse();

                    if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
                            || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
                        IndexResponse indexResponse = (IndexResponse) itemResponse;
                        //TODO 新增成功的处理
                        logger.info("新增成功,{}", indexResponse.toString());

                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
                        UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                        //TODO 修改成功的处理
                        logger.info("修改成功,{}", updateResponse.toString());

                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
                        DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
                        //TODO 删除成功的处理
                        logger.info("删除成功,{}", deleteResponse.toString());
                    }
                }
            }


            //异步方式发送批量操作请求
            /*
            ActionListener<BulkResponse> listener = new ActionListener<BulkResponse>() {
                @Override
                public void onResponse(BulkResponse bulkResponse) {

                }

                @Override
                public void onFailure(Exception e) {

                }
            };
            client.bulkAsync(request, listener);
            */

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
