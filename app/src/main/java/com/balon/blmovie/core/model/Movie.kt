package com.balon.blmovie.core.model.response

import kotlinx.serialization.Serializable

/**
 * 影片模型
 * {
 *     "actor": "神月柚莉爱,村濑步,阿座上洋平,白石凉子,野岛裕史,稗田宁宁",
 *     "area": "日本",
 *     "description": "<p>中学一年级的鸿田新某一天与名为库兹马的谜之生物相遇了。它既能说日语又能说俄语，还长得像动物似的。这家伙到底是什么呀！？</p>",
 *     "director": "野亦则行",
 *     "lang": "日语",
 *     "lastTime": "2026-07-02T23:58:01",
 *     "name": "库兹马唱歌的话家里哆啰啰",
 *     "note": "已完结",
 *     "pic": "https://img.lzipic.com/upload/vod/20260409-1/b5e37f2d9ffdeebb24aace021dc1575d.webp",
 *     "playFlag": "lzm3u8",
 *     "playUrl": "第01集$https://v.lzcdn27.com/20260409/8429_a2d78aa3/index.m3u8#第02集$https://v.lzcdn27.com/20260416/8818_5b313161/index.m3u8#第03集$https://v.lzcdn27.com/20260423/9184_549e7b52/index.m3u8#第04集$https://v.lzcdn27.com/20260430/9575_7dfc5e8a/index.m3u8#第05集$https://v.lzcdn27.com/20260507/9956_b838c819/index.m3u8#第06集$https://v.lzcdn27.com/20260514/10309_ea02b306/index.m3u8#第07集$https://v.lzcdn27.com/20260521/10660_ee12e18d/index.m3u8#第08集$https://v.lzcdn27.com/20260528/11071_07c572c0/index.m3u8#第09集$https://v.lzcdn27.com/20260604/11373_be357a5e/index.m3u8#第10集$https://v.lzcdn27.com/20260611/11746_3da346d2/index.m3u8#第11集$https://v.lzcdn31.com/20260618/7443_9db88129/index.m3u8#第12集$https://v.lzcdn27.com/20260625/12104_eedcd0ab/index.m3u8",
 *     "state": "",
 *     "tid": 30,
 *     "typeName": "日韩动漫",
 *     "vodId": 133458,
 *     "year": "2026"
 *   }
 */
@Serializable
data class Movie(
    /**
     * 演员
     */
    val actor: String?=null,
    /**
     * 地区
     */
    val area: String?=null,
    /**
     * 描述
     */
    val description: String?=null,
    /**
     * 导演
     */
    val director: String?=null,
    /**
     * 语言
     */
    val lang: String?=null,
    /**
     * 更新时间
     */
    val lastTime: String?=null,
    /**
     * 名字
     */
    val name: String?=null,
    /**
     * 注释，也是状态，比如已完结这种
     */
    val note: String?=null,
    /**
     * 封面
     */
    val pic: String?=null,
    /**
     * 播放来源
     */
    val playFlag: String?=null,
    /**
     * 播放地址
     */
    val playUrl: String?=null,
    /**
     * 状态
     */
    val state: String?=null,
    /**
     * 类型id
     */
    val tid: Int?=null,
    /**
     * 类型名称
     */
    val typeName: String,
    /**
     * 影片ID
     */
    val vodId: Long,
    /**
     * 年份
     */
    val year: String?=null
)