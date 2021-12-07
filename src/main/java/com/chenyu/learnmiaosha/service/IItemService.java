package com.chenyu.learnmiaosha.service;

import com.chenyu.learnmiaosha.pojo.model.ItemModel;

import java.util.List;

/**
 * 商品服务层接口
 *
 */
public interface IItemService {

    public ItemModel createItem(ItemModel itemModel);

    List<ItemModel> listItem();

    ItemModel getItemById(Integer id);

    ItemModel getItemByIdInCache(Integer id);

    boolean decreaseStock(Integer itemId,Integer amount);

    boolean increaseStock(Integer itemId,Integer amount);



    void increaseSales(Integer itemId,Integer amount);

    String initStockLog(Integer itemId,Integer amount);

}
