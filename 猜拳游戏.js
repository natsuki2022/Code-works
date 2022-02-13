/***
area 区域
stone 石头    石头 = 石头  石头 > 剪刀  石头 < 布
scissors 剪刀 剪刀 < 石头  剪刀 = 剪刀  剪刀 > 布
cloth 布      布 > 石头  布 < 剪刀  布 = 布
***/

/***
查看数据类型：Object.prototype.toString.call(变量)
刷新局部：window.location.reload('#area');
***/


function Init () {
  //获取并且绑定HTML的ID,返回HTML格式（HTMLDivElement）
  const area = document.querySelector("#area");
  const results = document.querySelector("#results");
  const stone = document.querySelector("#stone");
  const scissors = document.querySelector("#scissors");
  const cloth = document.querySelector("#cloth");

  //定义拖拽的卡牌
  let ondragstart_ID = null
  //猜拳类型编写成数组
  const random_Action = ['stone', 'scissors', 'cloth'];
  //随机获取数组中的一个数组的键
  const random_Digital = Math.round(Math.random() * (random_Action.length - 1) + 1);
  //获取数组中的键值，如数组random_Action中的'stone'（random_Action[0]）
  const random_Value = random_Action[random_Digital-1];

  //编写猜拳类型的方法
  function attribute (parameter) {
      //鼠标移入时（猜拳卡片变大）
      parameter.onmouseover = function () {
          this.style.height = '200px';
          this.style.width = '150px';
      }
      //鼠标移出时（猜拳卡片恢复初始状态）
      parameter.onmouseleave = function () {
          this.style.height = '150px';
          this.style.width = '100px';
      }
      //元素开始拖动时（猜拳卡片变透明）
      parameter.ondragstart = function () {
          this.style.opacity = '0.3';
          ondragstart_ID = parameter.id
      }
  }
  //创建猜拳类型的对象，给猜拳对象的属性赋值
  this.show_attribute = function () {
      attribute(stone)
      attribute(scissors)
      attribute(cloth)
  }
  //编写卡牌拖动事件
  this.overout = function () {
      //当卡牌拖拽到area（猜拳区域）之上
      area.ondragenter = function () {
          //判断随机数random_Digital，不能等于null
         if (random_Digital !== null) {
             //判断拖拽的卡牌
             if (ondragstart_ID === 'stone') {
                 //判断随机数对等于哪一个
                 switch (random_Value) {
                     case stone.id:
                         results.innerHTML = 'stone = stone,平局！';
                         break;
                     case scissors.id:
                         results.innerHTML = 'stone > scissors,你赢了！';
                         break;
                     case cloth.id:
                         results.innerHTML = 'stone < cloth,你输了！';
                         break;
                     default:
                         //刷新
                         window.location.reload();
                 }
                 //元素拖动结束（猜拳卡片恢复初始状态）
                 stone.ondragend = function () {
                     this.style.opacity = '1';
                 }
                 //延迟1秒后刷新
                 setTimeout(function (){
                     window.location.reload();
                 }, 1000);
                 //判断拖拽的卡牌
             }else if (ondragstart_ID === 'scissors') {
                 //判断随机数对等于哪一个
                 switch (random_Value) {
                     case stone.id:
                         results.innerHTML = 'scissors < stone,你输了！';
                         break;
                     case scissors.id:
                         results.innerHTML = 'scissors = scissors,平局！';
                         break;
                     case cloth.id:
                         results.innerHTML = 'scissors > cloth,你赢了！';
                         break;
                     default:
                         //刷新
                         window.location.reload();
                 }
                 //元素拖动结束（猜拳卡片恢复初始状态）
                 scissors.ondragend = function () {
                     this.style.opacity = '1';
                 }
                 //延迟1秒后刷新
                 setTimeout(function (){
                     window.location.reload();
                 }, 1000);
                 //判断拖拽的卡牌
             }else if (ondragstart_ID === 'cloth') {
                 //判断随机数对等于哪一个
                 switch (random_Value) {
                     case stone.id:
                         results.innerHTML = 'cloth > stone,你赢了！';
                         break;
                     case scissors.id:
                         results.innerHTML = 'cloth < scissors,你输了！';
                         break;
                     case cloth.id:
                         results.innerHTML = 'cloth = cloth,平局！';
                         break;
                     default:
                         //刷新
                         window.location.reload();
                 }
                 //元素拖动结束（猜拳卡片恢复初始状态）
                 cloth.ondragend = function () {
                     this.style.opacity = '1';
                 }
                 //延迟1秒后刷新
                 setTimeout(function (){
                     window.location.reload();
                 }, 1000);
             }
         }
      }
  }
}

//调用函数
function show() {
  const show_html = new Init();
  show_html.show_attribute()
  show_html.overout()
}
