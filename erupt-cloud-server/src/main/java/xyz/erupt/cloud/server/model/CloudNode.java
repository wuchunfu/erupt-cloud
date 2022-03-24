package xyz.erupt.cloud.server.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.stereotype.Component;
import xyz.erupt.annotation.Erupt;
import xyz.erupt.annotation.EruptField;
import xyz.erupt.annotation.fun.DataProxy;
import xyz.erupt.annotation.fun.TagsFetchHandler;
import xyz.erupt.annotation.sub_field.*;
import xyz.erupt.annotation.sub_field.sub_edit.BoolType;
import xyz.erupt.annotation.sub_field.sub_edit.Search;
import xyz.erupt.annotation.sub_field.sub_edit.TagsType;
import xyz.erupt.cloud.server.node.MetaNode;
import xyz.erupt.cloud.server.node.NodeManager;
import xyz.erupt.core.util.Erupts;
import xyz.erupt.jpa.dao.EruptDao;
import xyz.erupt.jpa.model.MetaModelUpdateVo;

import javax.annotation.Resource;
import javax.persistence.*;
import java.util.Collection;
import java.util.List;
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
@Component
public class CloudNode extends MetaModelUpdateVo implements DataProxy<CloudNode>, TagsFetchHandler {

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
            edit = @Edit(title = "负责人", type = EditType.TAGS,
                    tagsType = @TagsType(fetchHandler = CloudNode.class), notNull = true)
    )
    private String duty;

    @Transient
    @EruptField(
            views = @View(title = "Erupt 类数量", className = "text-center")
    )
    private Integer eruptNum;

    @Transient
    @EruptField(
            views = @View(title = "实例数量", className = "text-center")
    )
    private Integer instanceNum;

    @Transient
    @EruptField(
            views = @View(title = "请求次数", className = "text-center")
    )
    private Integer netCount;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @EruptField(
            views = @View(title = "描述", type = ViewType.HTML),
            edit = @Edit(title = "描述", type = EditType.TEXTAREA)
    )
    private String remark;

    @Transient
    @Resource
    private NodeManager nodeManager;

    @Transient
    @Resource
    private EruptDao eruptDao;

    @Override
    public void beforeAdd(CloudNode cloudNode) {
        cloudNode.setAccessToken(Erupts.generateCode(16));
    }

    @Override
    public void afterFetch(Collection<Map<String, Object>> list) {
        for (Map<String, Object> map : list) {
            MetaNode metaNode = nodeManager.getNode(map.get(NODE_NAME).toString());
            if (null == metaNode) {
                map.put("eruptNum", '-');
                map.put("instanceNum", '-');
                map.put("netCount", '-');
            } else {
                map.put("eruptNum", metaNode.getErupts().size());
                map.put("instanceNum", metaNode.getLocations().size());
                map.put("netCount", metaNode.getCount());
            }
        }
    }

    @Override
    public void afterDelete(CloudNode cloudNode) {
        nodeManager.removeNode(cloudNode.getNodeName());
    }

    @Override
    public List<String> fetchTags(String[] params) {
        return eruptDao.getJdbcTemplate().queryForList("select name from e_upms_user", String.class);
    }
}
