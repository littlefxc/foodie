package com.fengxuechao.controller;

import com.fengxuechao.pojo.Carousel;
import com.fengxuechao.pojo.Category;
import com.fengxuechao.pojo.vo.CategoryVO;
import com.fengxuechao.pojo.vo.NewItemsVO;
import com.fengxuechao.service.CarouselService;
import com.fengxuechao.service.CategoryService;
import com.fengxuechao.utils.JsonUtils;
import com.fengxuechao.utils.RedisOperator;
import com.fengxuechao.utils.ResultBean;
import com.fengxuechao.utils.enums.YesOrNo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengxuechao
 */
@Api(value = "首页", tags = {"首页展示的相关接口"})
@RestController
@RequestMapping("index")
public class IndexController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 如何更新缓存？
     * <ol>
     *     <li>后台运营系统，一旦广告（轮播图）发生更改，就可以删除缓存，然后重置</li>
     *     <li>定时重置，比如每天凌晨3点重置</li>
     *     <li>每个轮播图都有可能是一个广告，每个广告都会有一个过期时间，过期了，再重置</li>
     * </ol>
     *
     * @return
     */
    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public ResultBean<Object> carousel() {
        String key = "carousel";
        String value = redisOperator.get(key);
        log.info("获取首页轮播图列表 - 获取缓存 - value = {}", value);
        if (StringUtils.isNotBlank(value)) {
            List<Carousel> carousels = JsonUtils.jsonToList(value, Carousel.class);
            return ResultBean.ok(carousels);
        }
        List<Carousel> list = new ArrayList<>();
        list = carouselService.queryAll(YesOrNo.YES.type);
        String json = JsonUtils.objectToJson(list);
        redisOperator.set(key, json);
        return ResultBean.ok(list);
    }

    /**
     * 首页分类展示需求：
     * 1. 第一次刷新主页查询大分类，渲染展示到首页
     * 2. 如果鼠标上移到大分类，则加载其子分类的内容，如果已经存在子分类，则不需要加载（懒加载）
     */
    @ApiOperation(value = "获取商品分类(一级分类)", notes = "获取商品分类(一级分类)", httpMethod = "GET")
    @GetMapping("/cats")
    public ResultBean<Object> cats() {
        List<Category> list = new ArrayList<>();
        String catsStr = redisOperator.get("cats");
        if (StringUtils.isBlank(catsStr)) {
            redisOperator.set("cats", JsonUtils.objectToJson(list));
        } else {
            list = JsonUtils.jsonToList(catsStr, Category.class);
        }
        return ResultBean.ok(list);
    }

    @ApiOperation(value = "获取商品子分类", notes = "获取商品子分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public ResultBean<Object> subCat(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null) {
            return ResultBean.errorMsg("分类不存在");
        }

        List<CategoryVO> list = new ArrayList<>();
        String catsStr = redisOperator.get("subCat:" + rootCatId);
        if (StringUtils.isBlank(catsStr)) {
            list = categoryService.getSubCatList(rootCatId);

            /*
             * 查询的key在redis中不存在，
             * 对应的id在数据库也不存在，
             * 此时被非法用户进行攻击，大量的请求会直接打在db上，
             * 造成宕机，从而影响整个系统，
             * 这种现象称之为缓存穿透。
             * 解决方案：把空的数据也缓存起来，比如空字符串，空对象，空数组或list
             */
            if (list != null && list.size() > 0) {
                redisOperator.set("subCat:" + rootCatId, JsonUtils.objectToJson(list));
            } else {
                redisOperator.set("subCat:" + rootCatId, JsonUtils.objectToJson(list), 5*60);
            }
        } else {
            list = JsonUtils.jsonToList(catsStr, CategoryVO.class);
        }

        return ResultBean.ok(list);
    }

    @ApiOperation(value = "查询每个一级分类下的最新6条商品数据", notes = "查询每个一级分类下的最新6条商品数据", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public ResultBean<Object> sixNewItems(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null) {
            return ResultBean.errorMsg("分类不存在");
        }

        List<NewItemsVO> list = categoryService.getSixNewItemsLazy(rootCatId);
        return ResultBean.ok(list);
    }

}
