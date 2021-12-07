package com.chenyu.learnmiaosha.controller;

import com.chenyu.learnmiaosha.execption.BusinessException;
import com.chenyu.learnmiaosha.pojo.model.ItemModel;
import com.chenyu.learnmiaosha.pojo.viewobject.ItemVO;
import com.chenyu.learnmiaosha.response.CommonReturnType;
import com.chenyu.learnmiaosha.service.ICacheService;
import com.chenyu.learnmiaosha.service.IItemService;
import com.chenyu.learnmiaosha.service.IPromoService;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品Controller
 *
 * @author chen yu
 * @create 2021-12-05 11:03
 */
@RestController
@RequestMapping("/item")
public class ItemController  extends BaseController{


    @Autowired
    private IItemService itemService;

    @Autowired
    private ICacheService cacheService;


    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 创建商品
     * done
     * todo 参数放到body中
     *
     */

    @PostMapping(value = "/create",consumes={CONTENT_TYPE_FORMED})
    public CommonReturnType createItem(@RequestParam(name = "title")String title,
                                       @RequestParam(name = "description")String description,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock")Integer stock,
                                       @RequestParam(name = "imgUrl")String imgUrl) throws BusinessException {
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);
        ItemModel itemModelForReturn = itemService.createItem(itemModel);
        ItemVO itemVO = convertVOFromModel(itemModelForReturn);
        return CommonReturnType.create(itemVO);
    }


    /**
     * 根据ID  获取商品详情
     *
     */
    @GetMapping(value = "/get")
    public CommonReturnType getItem(@RequestParam(name = "id")Integer id){
        ItemModel itemModel = null;
        //先取本地缓存
        itemModel = (ItemModel) cacheService.getFromCommonCache("item_"+id);
        if(itemModel == null){
            //根据商品的id到redis内获取
            itemModel = (ItemModel) redisTemplate.opsForValue().get("item_"+id);
            //若redis内不存在对应的itemModel,则访问下游service
            if(itemModel == null){
                itemModel = itemService.getItemById(id);
                //设置itemModel到redis内
                redisTemplate.opsForValue().set("item_"+id,itemModel);
                redisTemplate.expire("item_"+id,10, TimeUnit.MINUTES);
            }
            //填充本地缓存
            cacheService.setCommonCache("item_"+id,itemModel);
        }
        ItemVO itemVO = convertVOFromModel(itemModel);
        return CommonReturnType.create(itemVO);
    }



    /**
     * 获取商品列表
     *
     */
    @GetMapping(value = "/list")
    public CommonReturnType listItem(){
        List<ItemModel> itemModelList = itemService.listItem();
        List<ItemVO> itemVOList =  itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = this.convertVOFromModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }




    /**
     * 对象转换
     *
     */
    private ItemVO convertVOFromModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);
        if(itemModel.getPromoModel() != null){
            //有正在进行或即将进行的秒杀活动
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else{
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }



}
