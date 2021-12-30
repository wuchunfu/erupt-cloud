package xyz.erupt.cloud.server.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.Type;
import xyz.erupt.annotation.Erupt;
import xyz.erupt.annotation.EruptField;
import xyz.erupt.annotation.fun.DataProxy;
import xyz.erupt.annotation.sub_field.*;
import xyz.erupt.annotation.sub_field.sub_edit.BoolType;
import xyz.erupt.annotation.sub_field.sub_edit.Search;
import xyz.erupt.upms.helper.HyperModelUpdateVo;

import javax.persistence.*;

/**
 * @author YuePeng
 * date 2021/12/16 00:28
 */
@Getter
@Setter
@Entity
@Table(name = "e_cloud_client", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
@Erupt(name = "服务管理")
public class CloudClient extends HyperModelUpdateVo implements DataProxy<CloudClient> {

    @EruptField(
            views = @View(title = "编码", sortable = true),
            edit = @Edit(title = "编码", notNull = true, search = @Search(vague = true),
                    readonly = @Readonly)
    )
    private String code;

    @EruptField(
            views = @View(title = "名称", sortable = true),
            edit = @Edit(title = "名称", notNull = true, search = @Search(vague = true))
    )
    private String name;

    @EruptField(
            views = @View(title = "秘钥", sortable = true, type = ViewType.HTML),
            edit = @Edit(title = "秘钥", readonly = @Readonly)
    )
    private String secret;

    @EruptField(
            views = @View(title = "状态", sortable = true, type = ViewType.HTML),
            edit = @Edit(title = "状态", notNull = true, boolType = @BoolType(
                    trueText = "启用", falseText = "禁用"
            ))
    )
    private Boolean status;

    @Transient
    @EruptField(
            views = @View(title = "Erupt类数量", sortable = true, type = ViewType.HTML)
    )
    private String eruptNum;

    @Transient
    @EruptField(
            views = @View(title = "实例数量", sortable = true, type = ViewType.HTML)
    )
    private String instanceNum;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @EruptField(
            views = @View(title = "备注"),
            edit = @Edit(title = "备注", type = EditType.HTML_EDITOR)
    )
    private String remark;

    @Override
    public void beforeAdd(CloudClient cloudClient) {
        cloudClient.setSecret(RandomStringUtils.randomAlphanumeric(8));
    }
}
