package com.yijian.dzpoker.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.yijian.dzpoker.R;

/**
 * Created by c_huangl on 0012, 11/12/2017.
 */

public class CardsUtil {

    public static Bitmap drawSingleCard(Context context, int suit, int member) {

        Bitmap newBitmap = Bitmap.createBitmap(88,120, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(newBitmap);
        // 获取资源文件的引用res
        Resources res = context.getResources();
        //三张图的位置
        int iPicLocation[][]=new  int[3][2];
        iPicLocation[0][0]=0;
        iPicLocation[0][1]=1;
        iPicLocation[1][0]=4;
        iPicLocation[1][1]=35;
        iPicLocation[2][0]=30;
        iPicLocation[2][1]=55;

        //画背景
        Bitmap bmpBackground;
        if (member==11){
            bmpBackground= BitmapFactory.decodeResource(res, R.drawable.rank_fg_11);

        }else if (member==12){

            bmpBackground= BitmapFactory.decodeResource(res, R.drawable.rank_fg_12);

        }else if (member==13){
            bmpBackground= BitmapFactory.decodeResource(res, R.drawable.rank_fg_13);

        }else{
            bmpBackground= BitmapFactory.decodeResource(res, R.drawable.fg);
        }

        //画牌的背景
        canvas.drawBitmap(bmpBackground, 0, 0, null);
        switch (member){
            case 2:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_2), iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3), iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3), iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_2),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_2),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_2),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 3:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_3),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_3),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_3),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_3),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 4:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_4),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_4),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_4),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_4),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 5:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_5),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_5),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_5),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_5),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 6:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_6),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_6),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_6),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_6),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 7:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_7),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_7),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_7),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_7),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 8:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_8),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_8),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_8),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_8),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 9:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_9),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_9),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_9),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_9),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 10:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_10),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_10),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_10),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_10),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 11:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_11),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_11),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_11),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_11),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 12:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_12),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_12),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_12),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_12),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 13:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_13),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_13),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_13),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_13),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 14:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_14),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_14),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_14),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_14),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
        }




//        // 定义矩阵对象
//        Matrix matrix = new Matrix();
//        // 缩放原图
//        matrix.postScale(1f, 1f);
//        // 向左旋转45度，参数为正则向右旋转
//        matrix.postRotate(-30);
//        //bmp.getWidth(), 500分别表示重绘后的位图宽高
//        Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
//
//                matrix, true);
//
//        // 在画布上绘制旋转后的位图
//        //放在坐标为0,200的位置
//        canvas.drawBitmap(dstbmp,0, 0, null);
//        matrix = new Matrix();
//        // 缩放原图
//        matrix.postScale(1f, 1f);
//        matrix.postRotate(-15);
//        dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
//
//                matrix, true);
//
//        // 在画布上绘制旋转后的位图
//        //放在坐标为0,200的位置
//        canvas.drawBitmap(dstbmp, 40, 5, null);

        return newBitmap;
    }
}