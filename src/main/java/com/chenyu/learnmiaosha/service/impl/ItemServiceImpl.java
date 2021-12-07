package com.chenyu.learnmiaosha.service.impl;

import com.chenyu.learnmiaosha.Validator.ValidationResult;
import com.chenyu.learnmiaosha.Validator.ValidatorImpl;
import com.chenyu.learnmiaosha.constant.Constant;
import com.chenyu.learnmiaosha.execption.BusinessException;
import com.chenyu.learnmiaosha.execption.EmBusinessError;
import com.chenyu.learnmiaosha.mapper.ItemDOMapper;
import com.chenyu.learnmiaosha.mapper.ItemStockDOMapper;
import com.chenyu.learnmiaosha.mapper.StockLogDOMapper;
import com.chenyu.learnmiaosha.mq.MqProducer;
import com.chenyu.learnmiaosha.pojo.dao.ItemDO;
import com.chenyu.learnmiaosha.pojo.dao.ItemStockDO;
import com.chenyu.learnmiaosha.pojo.dao.StockLogDO;
import com.chenyu.learnmiaosha.pojo.model.ItemModel;
import com.chenyu.learnmiaosha.pojo.model.PromoModel;
import com.chenyu.learnmiaosha.service.IItemService;
import com.chenyu.learnmiaosha.service.IPromoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品服务层
 *
 * @author chen yu
 * @create 2021-12-06 10:48
 */

@Service
public class ItemServiceImpl implements IItemService {

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private IPromoService promoService;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private StockLogDOMapper stockLogDOMapper;


    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) {
        //校验入参
        ValidationResult result = validator.validate(itemModel);
        if (result.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }
        ItemDO itemDO = convertItemDOFromItemModel(itemModel);
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());
        ItemStockDO itemStockDO = this.convertItemStockDOFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);
        return this.getItemById(itemModel.getId());
    }


    /**
     * 查询商品列表
     *
     */
    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOList = itemDOMapper.listItem();
        List<ItemModel> itemModelList =  itemDOList.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel = this.convertModelFromDataObject(itemDO,itemStockDO);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }


    /**
     * 根据商品 id 获取商品信息
     *
     */
    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if(itemDO == null){
            return null;
        }
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
        ItemModel itemModel = convertModelFromDataObject(itemDO,itemStockDO);
        //获取活动商品信息
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());

        //商品没有开始才可以发布活动
        //todo 这里是为了测试方便进行了条件的放宽
        if(promoModel != null /*&& promoModel.getStatus().intValue() != 3*/){
            itemModel.setPromoModel(promoModel);
        }
        return itemModel;
    }


    /**
     * 从缓存中获取商品信息
     *
     */
    @Override
    public ItemModel getItemByIdInCache(Integer id) {
        ItemModel itemModel = (ItemModel) redisTemplate.opsForValue().get("item_validate_"+id);
        if(itemModel == null){
            itemModel = this.getItemById(id);
            redisTemplate.opsForValue().set("item_validate_"+id,itemModel);
            redisTemplate.expire("item_validate_"+id,10, TimeUnit.MINUTES);
        }
        return itemModel;
    }


    /**
     * 减库存
     *
     */
    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) {
        //int affectedRow =  itemStockDOMapper.decreaseStock(itemId,amount);
        long result = redisTemplate.opsForValue().increment("promo_item_stock_"+itemId,amount.intValue() * -1);
        if(result >0){
            //更新库存成功
            return true;
        }else if(result == 0){
            //打上库存已售罄的标识
            redisTemplate.opsForValue().set("promo_item_stock_invalid_"+itemId,"true");

            //更新库存成功
            return true;
        }else{
            //更新库存失败
            increaseStock(itemId,amount);
            return false;
        }
    }


    /**
     * 增加库存
     *
     */
    @Override
    public boolean increaseStock(Integer itemId, Integer amount) {
        return false;
    }




    /**
     * 增加销售量
     *
     */
    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) {
        itemDOMapper.increaseSales(itemId,amount);

    }


    /**
     * 初始化库存流水
     *
     */
    @Override
    @Transactional
    public String initStockLog(Integer itemId, Integer amount) {
        StockLogDO stockLogDO = new StockLogDO();
        stockLogDO.setItemId(itemId);
        stockLogDO.setAmount(amount);
        stockLogDO.setStockLogId(UUID.randomUUID().toString().replace("-",""));
        stockLogDO.setStatus(Constant.STOCK_LOG_STATE_INIT);
        stockLogDOMapper.insertSelective(stockLogDO);
        return stockLogDO.getStockLogId();
    }


    /**
     * 对象转换
     *
     */
    private ItemModel convertModelFromDataObject(ItemDO itemDO, ItemStockDO itemStockDO) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO, itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());

        return itemModel;
    }

    /**
     * 对象转换
     *
     */
    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());
        return itemStockDO;
    }


    /**
     * 对象转换
     *
     */
    private ItemDO convertItemDOFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel, itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }
}
