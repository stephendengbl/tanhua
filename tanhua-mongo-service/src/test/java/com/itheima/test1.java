package com.itheima;

import com.itheima.domain.mongo.TopicResult;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class test1 {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void TopicTest1() {

        TopicResult topicResult=new TopicResult();
        topicResult.setConclusion("猫头鹰：他们的共同特质为重计划、条理、细节精准。在行为上，表现出喜欢理性思考与分析、较重视制度、结构、规范。他们注重执行游戏规则、循规蹈矩、巨细靡遗、重视品质、敬业负责。");
        topicResult.setId(ObjectId.get());
        topicResult.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/tanhua/avatar_9.jpg");
        mongoTemplate.save(topicResult);
    }
    @Test
    public void TopicTest2() {

        TopicResult topicResult=new TopicResult();
        topicResult.setConclusion("白兔型：平易近人、敦厚可靠、避免冲突与不具批判性。在行为上，表现出不慌不忙、冷静自持的态度。他们注重稳定与中长程规划，现实生活中，常会反思自省并以和谐为中心，即使面对困境，亦能泰然自若，从容应付。");
        topicResult.setId(ObjectId.get());
        topicResult.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/tanhua/avatar_12.jpg");
        mongoTemplate.save(topicResult);
    }
    @Test
    public void TopicTest13() {

        TopicResult topicResult=new TopicResult();
        topicResult.setConclusion("狐狸型 ：人际关系能力极强，擅长以口语表达感受而引起共鸣，很会激励并带动气氛。他们喜欢跟别人互动，重视群体的归属感，基本上是比较「人际导向」。由于他们富同理心并乐于分享，具有很好的亲和力，在服务业、销售业、传播业及公共关系等领域中，狐狸型的领导者都有很杰出的表现。");
        topicResult.setId(ObjectId.get());
        topicResult.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/tanhua/avatar_7.jpg");
        mongoTemplate.save(topicResult);
    }
    @Test
    public void TopicTest14() {

        TopicResult topicResult=new TopicResult();
        topicResult.setConclusion("狮子型：性格为充满自信、竞争心强、主动且企图心强烈，是个有决断力的领导者。一般而言，狮子型的人胸怀大志，勇于冒险，看问题能够直指核心，并对目标全力以赴。\n" +
                "他们在领导风格及决策上，强调权威与果断，擅长危机处理，此种性格最适合开创性与改革性的工作。\n");
        topicResult.setId(ObjectId.get());
        topicResult.setCover("https://tanhua-v1.oss-cn-beijing.aliyuncs.com/images/logo/19.jpg");
        mongoTemplate.save(topicResult);
    }
    /*@Test
    public void TopicTest2() {
        TopicDetails topicDetails=new TopicDetails();
        topicDetails.setId(ObjectId.get());
        topicDetails.setQuestionId("60933edd7568a234f4947077");
        topicDetails.setContent("打架或挣扎");
        topicDetails.setScore(2);
        mongoTemplate.save(topicDetails);
    }
    @Test
    public void TopicTest3() {
        TopicDetails topicDetails=new TopicDetails();
        topicDetails.setId(ObjectId.get());
        topicDetails.setQuestionId("60933edd7568a234f4947077");
        topicDetails.setContent("找东西或人");
        topicDetails.setScore(3);
        mongoTemplate.save(topicDetails);
    }
    @Test
    public void TopicTest4() {
        TopicDetails topicDetails=new TopicDetails();
        topicDetails.setId(ObjectId.get());
        topicDetails.setQuestionId("60933edd7568a234f4947077");
        topicDetails.setContent("飞或漂浮");
        topicDetails.setScore(5);
        mongoTemplate.save(topicDetails);
    }
    @Test
    public void TopicTest5() {
        TopicDetails topicDetails=new TopicDetails();
        topicDetails.setId(ObjectId.get());
        topicDetails.setQuestionId("60933edd7568a234f4947077");
        topicDetails.setContent("你平常不做梦");
        topicDetails.setScore(6);
        mongoTemplate.save(topicDetails);
    }
    @Test
    public void TopicTest6() {
        TopicDetails topicDetails=new TopicDetails();
        topicDetails.setId(ObjectId.get());
        topicDetails.setQuestionId("60933edd7568a234f4947077");
        topicDetails.setContent("你的梦都是愉快的");
        topicDetails.setScore(1);
        mongoTemplate.save(topicDetails);
    }*/
    /*@Test
    public void TopicTest7() {
        TopicDetails topicDetails=new TopicDetails();
        topicDetails.setId(ObjectId.get());
        topicDetails.setQuestionId("60933edd7568a234f4947075");
        topicDetails.setContent("棕色或灰色");
        topicDetails.setScore(1);
        mongoTemplate.save(topicDetails);
    }
    @Test
    public void TopicTest8() {
        TopicDetails topicDetails=new TopicDetails();
        topicDetails.setId(ObjectId.get());
        topicDetails.setQuestionId("60933edd7568a234f4947076");
        topicDetails.setContent("仰躺，伸直");
        topicDetails.setScore(7);
        mongoTemplate.save(topicDetails);
    }
    @Test
    public void TopicTest9() {
        TopicDetails topicDetails=new TopicDetails();
        topicDetails.setId(ObjectId.get());
        topicDetails.setQuestionId("60933edd7568a234f4947076");
        topicDetails.setContent("俯躺，伸直 ");
        topicDetails.setScore(6);
        mongoTemplate.save(topicDetails);
    }
    @Test
    public void TopicTest10() {
        TopicDetails topicDetails=new TopicDetails();
        topicDetails.setId(ObjectId.get());
        topicDetails.setQuestionId("60933edd7568a234f4947076");
        topicDetails.setContent("侧躺，微蜷");
        topicDetails.setScore(4);
        mongoTemplate.save(topicDetails);
    }
    @Test
    public void TopicTest11(){
        TopicDetails topicDetails=new TopicDetails();
        topicDetails.setId(ObjectId.get());
        topicDetails.setQuestionId("60933edd7568a234f4947076");
        topicDetails.setContent("头睡在一手臂上");
        topicDetails.setScore(2);
        mongoTemplate.save(topicDetails);
    }
    @Test
    public void TopicTest12(){
        TopicDetails topicDetails=new TopicDetails();
        topicDetails.setId(ObjectId.get());
        topicDetails.setQuestionId("60933edd7568a234f4947076");
        topicDetails.setContent("被子盖过头");
        topicDetails.setScore(1);
        mongoTemplate.save(topicDetails);
    }*/
    /*@Test
    public void TopicTest13(){
        TopicDetails topicDetails=new TopicDetails();
        topicDetails.setId(ObjectId.get());
        topicDetails.setQuestionId("60933edd7568a234f4947072");
        topicDetails.setContent("感到非常愤怒");
        topicDetails.setScore(2);
        mongoTemplate.save(topicDetails);
    }
    @Test
    public void TopicTest14(){
        TopicDetails topicDetails=new TopicDetails();
        topicDetails.setId(ObjectId.get());
        topicDetails.setQuestionId("60933edd7568a234f4947072");
        topicDetails.setContent("在上述两极端之间");
        topicDetails.setScore(4);
        mongoTemplate.save(topicDetails);
    }*/
   /* @Test
    public void QuestionTest1() {
        TopicQuestionnaire t = new TopicQuestionnaire();
        t.setName("初级灵魂题");
        t.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/tanhua/avatar_9.jpg");//封面
        t.setLevel("1");//级别
        t.setStar(2);//星级
        mongoTemplate.save(t);
    }
    @Test
    public void QuestionTest2() {
        TopicQuestionnaire t = new TopicQuestionnaire();
        t.setName("中级灵魂题");
        t.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/tanhua/avatar_9.jpg");//封面
        t.setLevel("2");//级别
        t.setStar(3);//星级
        mongoTemplate.save(t);
    }
    @Test
    public void QuestionTest3() {
        TopicQuestionnaire t = new TopicQuestionnaire();
        t.setName("高级灵魂题");
        t.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/tanhua/avatar_9.jpg");//封面
        t.setLevel("3");//级别
        t.setStar(4);//星级
        mongoTemplate.save(t);
    }*/
}
