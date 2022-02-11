package xyz.erupt.cloud.server.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import xyz.erupt.annotation.Erupt;
import xyz.erupt.annotation.EruptField;
import xyz.erupt.annotation.fun.DataProxy;
import xyz.erupt.annotation.sub_field.Edit;
import xyz.erupt.annotation.sub_field.EditType;
import xyz.erupt.annotation.sub_field.Readonly;
import xyz.erupt.annotation.sub_field.View;
import xyz.erupt.annotation.sub_field.sub_edit.BoolType;
import xyz.erupt.annotation.sub_field.sub_edit.Search;
import xyz.erupt.cloud.server.base.MetaNode;
import xyz.erupt.cloud.server.node.NodeManager;
import xyz.erupt.core.util.Erupts;
import xyz.erupt.jpa.model.MetaModelUpdateVo;

import javax.persistence.*;
import java.util.Collection;
import java.util.Map;

/**
 * @author YuePeng
 * date 2021/12/16 00:28
 */
@Getter
@Setter
@Entity
@Table(name = "e_cloud_node", uniqueConstraints = @UniqueConstraint(columnNames = CloudNode.NODE_NAME))
@Erupt(name = "节点管理", dataProxy = CloudNode.class)
public class CloudNode extends MetaModelUpdateVo implements DataProxy<CloudNode> {

    public static final String NODE_NAME = "nodeName";

    @EruptField(
            views = @View(title = "节点名", sortable = true),
            edit = @Edit(title = "节点名", desc = "NodeName", notNull = true, search = @Search(vague = true))
    )
    private String nodeName;

    @EruptField(
            views = @View(title = "名称", sortable = true),
            edit = @Edit(title = "名称", notNull = true, search = @Search(vague = true))
    )
    private String name;

    @EruptField(
            views = @View(title = "Access Token", width = "170px"),
            edit = @Edit(title = "Access Token", readonly = @Readonly, search = @Search, show = false)
    )
    private String accessToken;

    @ManyToOne
    @EruptField(
            views = @View(title = "所属分组", column = "name"),
            edit = @Edit(title = "所属分组", type = EditType.REFERENCE_TREE, search = @Search)
    )
    private CloudNodeGroup cloudNodeGroup;

    @EruptField(
            views = @View(title = "状态", sortable = true),
            edit = @Edit(title = "状态", search = @Search, notNull = true, boolType = @BoolType(
                    trueText = "启用", falseText = "禁用"
            ))
    )
    private Boolean status = true;

    @EruptField(
            views = @View(title = "负责人", sortable = true),
            edit = @Edit(title = "负责人", type = EditType.TAGS, notNull = true)
    )
    private String duty;

    @Transient
    @EruptField(
            views = @View(title = "Erupt Num")
    )
    private Integer eruptNum;

    @Transient
    @EruptField(
            views = @View(title = "实例数量")
    )
    private Integer instanceNum;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @EruptField(
            views = @View(title = "描述"),
            edit = @Edit(title = "描述", type = EditType.HTML_EDITOR)
    )
    private String remark;

    @Override
    public void beforeAdd(CloudNode cloudNode) {
        cloudNode.setAccessToken(Erupts.generateCode(16));
    }

    @Override
    public void afterFetch(Collection<Map<String, Object>> list) {
        for (Map<String, Object> map : list) {
            MetaNode metaNode = NodeManager.getNode(map.get(NODE_NAME).toString());
            if (null == metaNode) {
                map.put("instanceNum", 0);
            } else {
                map.put("eruptNum", metaNode.getErupts().size());
                map.put("instanceNum", metaNode.getLocations().size());
            }
        }
    }
}
