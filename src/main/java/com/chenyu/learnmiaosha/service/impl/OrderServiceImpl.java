package com.chenyu.learnmiaosha.service.impl;

import com.chenyu.learnmiaosha.constant.Constant;
import com.chenyu.learnmiaosha.execption.BusinessException;
import com.chenyu.learnmiaosha.execption.EmBusinessError;
import com.chenyu.learnmiaosha.mapper.OrderDOMapper;
import com.chenyu.learnmiaosha.mapper.SequenceDOMapper;
import com.chenyu.learnmiaosha.mapper.StockLogDOMapper;
import com.chenyu.learnmiaosha.pojo.dao.OrderDO;
import com.chenyu.learnmiaosha.pojo.dao.SequenceDO;
import com.chenyu.learnmiaosha.pojo.dao.StockLogDO;
import com.chenyu.learnmiaosha.pojo.model.ItemModel;
import com.chenyu.learnmiaosha.pojo.model.OrderModel;
import com.chenyu.learnmiaosha.service.IItemService;
import com.chenyu.learnmiaosha.service.IOrderService;
import com.chenyu.learnmiaosha.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author chen yu
 * @create 2021-12-06 11:00
 */

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Autowired
    private IItemService itemService;


    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private StockLogDOMapper stockLogDOMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLogId) {
        //1.校验下单状态,下单的商品是否存在，用户是否合法，购买数量是否正确
        ItemModel itemModel = itemService.getItemByIdInCache(itemId);
        if(itemModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品信息不存在");
        }
        // 用户下单数量的限制
        if(amount <= 0 || amount > 99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"数量信息不正确");
        }
        //2.落单减库存
        //要进行锁库存 只要能够落单  则进行减去库存  而不是其他减库存的逻辑
        boolean result = itemService.decreaseStock(itemId,amount);
        if(!result){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }
        //3.订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        if(promoId != null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else{
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));

        //生成交易流水号,订单号
        orderModel.setId(generateOrderNo());
        OrderDO orderDO = convertFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);

        //加上商品的销量
        itemService.increaseSales(itemId,amount);
        //设置库存流水状态为成功
        StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
        if(stockLogDO == null){
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        }
        //交易日志
        stockLogDO.setStatus(Constant.STOCK_LOG_STATE_SUCCESS);
        stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO);
        //4.返回前端
        return orderModel;
    }

    /**
     * 生成下单流水号
     *
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateOrderNo(){
        //订单号有16位
        StringBuilder stringBuilder = new StringBuilder();
        //前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);
        //中间6位为自增序列
        //获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO =  sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        for(int i = 0; i < 6-sequenceStr.length();i++){
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);
        //最后2位为分库分表位,暂时写死
        stringBuilder.append("00");
        return stringBuilder.toString();
    }


    /**
     * 对象转换
     *
     */
    private OrderDO convertFromOrderModel(OrderModel orderModel){
        if(orderModel == null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderDO;
    }
}
