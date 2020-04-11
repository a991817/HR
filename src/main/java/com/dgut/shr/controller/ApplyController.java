package com.dgut.shr.controller;

import com.dgut.shr.config.Result;
import com.dgut.shr.javaBean.Apply;
import com.dgut.shr.service.ApplyService;
import com.dgut.shr.service.sys.RedisService;
import com.dgut.shr.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author shijie_liu
 * @date 2020/4/11 17:33
 */
@Controller
@RequestMapping("apply")
public class ApplyController {
    @Autowired
    ApplyService applyService;

    @Autowired
    RedisService redisService;
    /**
     * 提交申请
     * @param apply
     * @return
     */
    @RequestMapping("addApply")
    @ResponseBody
    public Result addApply(Apply apply, HttpServletRequest request){
        if (apply == null){
            return Result.APPLY_FAIL;
        }
        String empId = redisService.get(CookieUtil.getCookieValue(request,LoginController.TOKEN));
        apply.setEmployeeId(Long.valueOf(empId));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        apply.setDate(df.format(new Date()));
//        先判断该员工当天是否已经申请了相同类型的申请
        if (applyService.selectList(apply).size() > 0){
            return new Result("1","您已提交过相同的申请了");
        }
        int cols = applyService.insertApply(apply);
        if (cols > 0){
            return Result.APPLY_SUCCESS;
        }
        return Result.APPLY_FAIL;
    }

    /**
     * 审批申请
     * 前端只传申请id和state过来就可以了
     */
    @RequestMapping("approval")
    @ResponseBody
    public Result approval(Apply apply){
        if (apply == null){
            return Result.FAIL;
        }
        applyService.updateApply(apply);
        return Result.SUCCESS;
    }



}
