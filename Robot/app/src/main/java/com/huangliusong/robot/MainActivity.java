package com.huangliusong.robot;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import junit.framework.Test;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private StringBuffer sbuff;
    private ListView answer;
    private MyAdapter myAdapter;
    private ArrayList<TalkBean> mlist = new ArrayList<TalkBean>();
    private String[] mAnswer=new String[]{"要约么？","生活处处都有美女啦","抱歉，我拒绝回答"};
    private int[] mPics=new int[]{R.drawable.a,R.drawable.b};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        answer = (ListView) findViewById(R.id.answer);
        myAdapter = new MyAdapter();
        answer.setAdapter(myAdapter);
        // 将“12345678”替换成您申请的 APPID，申请地址：http://www.xfyun.cn
        // 请勿在“=”与 appid 之间添加任务空字符或者转义符
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=56adade1");
        compose("hello，欢迎使用小五助手");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //开始语音识别
    public void startListen(View v) {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, null);
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //若要将UI控件用于语义理解， 必须添加以下参数设置， 设置之后onResult回调返回将是语义理解
        //结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        sbuff = new StringBuffer();
        //3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {
            //最终的识别结果
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                String result = recognizerResult.getResultString();
                // tv1.setText("识别结果：" + recognizerResult.getResultString() + b);
                String spreak_word = pareData(result);
                sbuff.append(spreak_word+"  ");
                if (b) {
                    String askContent = sbuff.toString();//得到最终结果
                    Log.e("HLS","用户："+askContent);
                    TalkBean askBean = new TalkBean(askContent, -1, true);//初始化提问对象
                    mlist.add(askBean);
                    //刷新 listview
                    String answer="这个问题我们机器要开个会，等商量出来再告诉你哈";
                    int imageId=-1;
                    if(askContent.contains("你好"))
                    {
                         answer="你好呀";
                    }
                    else if(askContent.contains("你是谁"))
                    {
                         answer="我是超级宇宙无敌帅比小五助手！";
                    }
                    else if(askContent.contains("美女"))
                    {
                        //随机回答
                        int id=(int)(Math.random()*mAnswer.length);
                         answer=mAnswer[id];
                    }
                    else if(askContent.contains("发动机"))
                    {
                        answer="您的发动机和我的心脏一样有力";
                    }
                    else if(askContent.contains("法拉利好贵呀"))
                    {
                            answer="是呀，不过我很喜欢这个牌子";
                    }
                    else if(askContent.contains("六小龄童"))
                    {
                        imageId=mPics[1];
                        answer="六小龄童节目被毙 无缘春晚，春晚导演吕逸涛微博遭百万网友炮轰，网友纷纷在该微博下评论留言,呼吁六小龄童再次登上春晚的舞台";
                    }
                    else if(askContent.contains("谷歌股票"))
                    {
                        answer="开盘，731.53美元，成交量347.43万，2016-01-29 15:54:58 已收盘 (美东时间)";
                        imageId=mPics[0];
                    }
                    else if(askContent.contains("搜索黄柳"))
                    {
                        answer="信息工程学院，13级软件工程二班";
                    }
                    else if(askContent.contains("维基百科"))
                    {
                        answer="维基百科（Wikipedia），是一个基于维基技术的多语言百科全书协作计划，这是一部用多种语言编写的网络百科全书。维基百科一词取自于该网站核心技术“Wiki”以及具有百科全书之意的“encyclopedia”共同创造出来的新混成词“Wikipedia”";

                    }
                    else if(askContent.contains("春晚"))
                    {
                        answer="2016年春晚吉祥物以十二生肖“猴”为原型，取名“康康”。寓意祝愿全国观众在新的一年里康健安泰、康乐吉祥。在视觉上，“康康”首先给人一种可爱的直观印象，以中国传统水墨画的艺术形式，表现出“猴”的灵动活泼。整体配色也采用传统中国风，艺术风格独到，个性特征鲜明。";

                    }
                    else if(askContent.contains("你是猪"))
                    {
                        answer="猪可是生肖中最萌的属相哦";
                    }
                    else if(askContent.contains("你是"))
                    {
                        answer="我是小五助手";
                    }
                    TalkBean answerBean=new TalkBean(answer,imageId,false);
                    mlist.add(answerBean);
                    myAdapter.notifyDataSetChanged();
                    compose(answer);
                    Log.e("HLS", "助手："+answer);
                }
            }

            @Override
            public void onError(SpeechError speechError) {

            }
        });
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    private String pareData(String _json) {
        //Gson解析
        Gson gson = new Gson();
        VoiceBean voiceBean = gson.fromJson(_json, VoiceBean.class);
        StringBuffer sb = new StringBuffer();
        ArrayList<VoiceBean.WS> ws = voiceBean.ws;
        for (VoiceBean.WS w : ws) {
            String word = w.cw.get(0).w;
            sb.append(word);
        }
        return sb.toString();
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public TalkBean getItem(int position) {
            return mlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {

                convertView = View.inflate(getApplicationContext(), R.layout.list_iten, null);

                holder = new ViewHolder();
                holder.tvAsk = (TextView) convertView.findViewById(R.id.tv_ask);
                holder.tvAnswer = (TextView) convertView.findViewById(R.id.tv_answer);
                holder.llAnswer = (LinearLayout) convertView.findViewById(R.id.ll_answer);
                holder.ivPic = (ImageView) convertView.findViewById(R.id.tv_pic);
                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();
            }
            TalkBean item = getItem(position);
            if (item.isAsk) {
                //提问
                holder.tvAsk.setVisibility(View.VISIBLE);
                holder.llAnswer.setVisibility(View.GONE);
                holder.tvAsk.setText(item.content);
            } else {

                holder.tvAsk.setVisibility(View.GONE);
                holder.tvAnswer.setVisibility(View.VISIBLE);
                holder.tvAnswer.setText(item.content);
                //图片
                if (item.imageId > 0) {
                    holder.ivPic.setVisibility(View.VISIBLE);
                    holder.ivPic.setImageResource(item.imageId);
                } else {
                    holder.ivPic.setVisibility(View.GONE);
                }
            }

            return convertView;
        }
    }

    static class ViewHolder {
        public TextView tvAsk;
        public TextView tvAnswer;
        public ImageView ivPic;
        public LinearLayout llAnswer;

    }

    //语音合成
    public void compose(String speak) {
        //1.创建 SpeechSynthesizer 对象, 第二个参数：本地合成时传 InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(this, null);
        //2.合成参数设置，详见《MSC Reference Manual》SpeechSynthesizer 类
        //设置发音人（更多在线发音人，用户可参见 附录13.2
        mTts.setParameter(SpeechConstant.VOICE_NAME, "黄柳淞"); //设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //设置合成音频保存位置（可自定义保存位置） ，保存在“./sdcard/iflytek.pcm”
        //保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
        //仅支持保存为 pcm 和 wav 格式，如果不需要保存合成音频，注释该行代码
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
        //3.开始合成
        mTts.startSpeaking(speak, null);
        //合成监听器
        SynthesizerListener mSynListener = new SynthesizerListener() {
            //会话结束回调接口，没有错误时，error为null
            public void onCompleted(SpeechError error) {
            }
            //缓冲进度回调
            //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在
            // 文本中结束位置，info为附加信息。
            public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            }
            //开始播放
            public void onSpeakBegin() {
            }
            //暂停播放
            public void onSpeakPaused() {
            }
            //播放进度回调
            //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文
            //本中结束位置.
            public void onSpeakProgress(int percent, int beginPos, int endPos) {
            }
            //恢复播放回调接口
            public void onSpeakResumed() {
            }
            //会话事件回调接口
            public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
            }
        };
    }
}
