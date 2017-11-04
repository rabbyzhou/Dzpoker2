package com.yijian.dzpoker.view.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by koyabr on 10/22/15.
 */
public class GameUser implements Serializable {
    public int userId;
    public String userHeadPic;
    public String nickName;
    public int remainchips;
    public int seatindex;
}
