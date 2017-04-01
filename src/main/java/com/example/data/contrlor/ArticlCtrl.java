package com.example.data.contrlor;

import com.example.data.base.controller.BaseControllerImpl;
import com.example.data.common.UtilFun;
import com.example.data.entity.Article;
import com.example.data.entity.ArticleType;
import com.example.data.entity.user.User;
import com.example.data.service.IArticleService;
import com.example.data.service.IArticleTypeService;
import com.example.data.service.user.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by wanghuiwen on 17-3-11.
 */
@Controller
@RequestMapping(value = "/article")
public class ArticlCtrl extends BaseControllerImpl<Article, String> {
    @Resource
    private IArticleService articleService;
    @Resource
    private IArticleTypeService articleTypeService;

    @Resource
    private IUserService userService;

    @Override
    protected String setAddPage () {
        return "/blog/article/add";
    }

    @Override
    protected void setAddAttr (Article article) {

        article.setArticleDate (new Date ());
    }

    @Override
    protected void setAddPara (Model model) {

        List<ArticleType> list = articleTypeService.listByUser (getSessionUser ().getId ());
        model.addAttribute ("articleType",list);
    }


    @RequestMapping("/index")
    public String index() {
        return "/blog/index";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list (@RequestParam(defaultValue = "0") int pageNumber,Model model) {

        LinkedHashMap<String,Object> sql = new LinkedHashMap<String,Object> ();

        sql.put ("and user_id = ? ",getSessionUser ().getId ());

        Page<Article> page = articleService.PageByWhere (getPage (pageNumber),sql);

        List<ArticleType> list = articleTypeService.listByUser (getSessionUser ().getId ());

        model.addAttribute ("articleType",list);
        if (page != null) model.addAttribute (page);
        if (pageNumber > 0) {
            return "/blog/article/list_append";
        }
        return "/blog/article/list";
    }


    @RequestMapping(value = "/details")
    public String articleDetails (@RequestParam(defaultValue = "") Article id,Model model) {

        model.addAttribute (id);

        User user = userService.findOne (id.getUserId ());

        List<ArticleType> list = articleTypeService.listByUser (id.getUserId ());

        model.addAttribute ("articleType",list);

        model.addAttribute (user);

        return "/blog/article/details";
    }

    @RequestMapping(value = "/serch", method = RequestMethod.GET)
    public String serch(@RequestParam(defaultValue = "0") int pageNumber, Model model,
                        @RequestParam(defaultValue = "") String content) {

        Page<Article> page = articleService.serchByKey (getPage (pageNumber),content);

        model.addAttribute(page);
        return "/blog/article/list_append";
    }


    @RequestMapping(value = "/articleByType")
    public String articleByType (@RequestParam(defaultValue = "0") int pageNumber,@RequestParam(defaultValue = "") String typeId,Model model) {

        LinkedHashMap<String,Object> sql = new LinkedHashMap<String,Object> ();
        if (UtilFun.isEmptyString (typeId)) sql.put ("and type = ?",typeId);
        Page<Article> page = articleService.PageByWhere (getPage (pageNumber),sql);
        model.addAttribute (page);
        return "/blog/article/list";
    }

}
