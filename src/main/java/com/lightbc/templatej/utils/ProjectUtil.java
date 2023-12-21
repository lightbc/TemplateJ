package com.lightbc.templatej.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.wm.WindowManager;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取打开工程的信息工具类
 */
@Slf4j
public class ProjectUtil {

    /**
     * 获取项目对象
     *
     * @return Project-项目对象
     */
    @SuppressWarnings({"finally", "ReturnInsideFinallyBlock"})
    public static Project getProject() {
        // 项目管理器
        ProjectManager projectManager = ProjectManager.getInstance();
        // 默认项目
        Project project = projectManager.getDefaultProject();
        try {
            // 所有打开的项目
            Project[] projects = projectManager.getOpenProjects();
            // 窗体管理器
            WindowManager windowManager = WindowManager.getInstance();
            if (projects.length > 0) {
                for (Project p : projects) {
                    // 判断当前活动窗体的打开项目
                    Window window = windowManager.suggestParentWindow(p);
                    if (window != null && window.isActive()) {
                        project = p;
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取项目对象报错：{}", e.getMessage());
        } finally {
            return project;
        }
    }

    /**
     * 获取当前项目所有模块名称
     *
     * @return map
     */
    public static Map<String, Module> getModuleMap() {
        Map<String, Module> map = new HashMap<>();
        Module[] modules = ModuleManager.getInstance(getProject()).getModules();
        for (Module module : modules) {
            String name = module.getName();
            map.put(name, module);
        }
        return map;
    }

    /**
     * 通过模块获取模板路径信息
     *
     * @param module 模块
     * @return string
     */
    public static String getModulePath(Module module) {
        String path = ModuleUtil.getModuleDirPath(module);
        if (!"".equals(path.trim())) {
            return path;
        }
        ModuleRootManager manager = ModuleRootManager.getInstance(module);
        return manager.getModule().getProject().getBasePath();
    }
}
