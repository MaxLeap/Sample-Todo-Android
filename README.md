# MaxLeap-Sample-Todo-Android

## 简介

Todo 是 MaxLeap SDK 的一个 Sample，该项目依赖于 MaxLeap 的基础模块。通过该应用你可以学习和了解基于 MaxLeap SDK 的 Cloud Data 相关的操作，包括以下几部分：

- 如何进行注册和登陆
- 如何创建和删除 Todo List 和 Todo Item
- 如何将 Todo Item 关联到 Todo List 中对象
- 如何使用 Query 语句根据条件获得 Todo List 和 Todo Item 对象
- 如何在 ListView 中展示来自 MaxLeap Server 的数据

## 效果

<img src="capture/todos.gif" alt="capture" style="width: 200px;"/>

## 使用

1. 前往 [MaxLeap 开发者控制台](https://maxleap.cn)，创建一个 Android 应用，获得你自己的 `APP ID` 和 `API KEY`。
2. 打开 Android Studio 或 IDEA ，点击菜单项 `File -> Open` 选择 `setting.gradle` 文件导入工程。
3. 打开 `App.java` 文件，使用你自己的 `APP ID` 和 `API KEY` 替换该文件中已定义的同名常量。
4. 在 [MaxLeap 开发者控制台](https://maxleap.cn) 跳转到开发者中心页面，创建以下必要的表结构：
    
    **表名**：Items

	列名 | 类型
    -----|-----
    Name | String
    Status | Boolean

    **表名**：Lists

	列名 | 类型
    -----|-----
    Name | String
    Items | Relation 指向 Items
5. 运行应用


