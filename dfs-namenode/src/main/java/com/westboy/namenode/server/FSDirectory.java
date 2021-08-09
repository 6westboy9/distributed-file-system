package com.westboy.namenode.server;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.westboy.common.constant.GlobalConstant;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 负责管理内存中的文件目录树的核心组件
 *
 * @author pengbo
 * @since 2021/1/28
 */
public class FSDirectory {

    public static final String DIRECTORY_DELIMITER = "/";
    private static final String EMPTY_STR = "";

    private INodeDirectory dirTree;
    /**
     * 文件目录树的读写锁
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public FSDirectory() {
        this.dirTree = new INodeDirectory(EMPTY_STR);
    }

    public void setDirTree(INodeDirectory dirTree) {
        this.dirTree = dirTree;
    }

    public void writeLock() {
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        lock.writeLock().unlock();
    }

    public void readLock() {
        lock.readLock().lock();
    }

    public void readUnlock() {
        lock.readLock().unlock();
    }

    public INodeDirectory getDirTree() {
        return dirTree;
    }

    public void mkdir(String path) {
        // path = /usr/warehouse/hive
        // 你应该先判断一下，"/" 根目录下有没有一个 "usr" 目录的存在
        // 如果说有的话，那么再判断一下，"/usr" 目录下，有没有一个 "/warehouse" 目录的存在
        // 如果说没有，那么就得先创建一个 "/warehouse" 对应的目录，挂在 "/usr" 目录下
        // 接着再对 "/hive" 这个目录创建一个节点挂载上去

        try {
            writeLock();
            String[] splitPaths = path.split(DIRECTORY_DELIMITER);
            INodeDirectory parent = dirTree;
            for (String splitPath : splitPaths) {
                // 空串为根路径
                if (EMPTY_STR.equals(splitPath.trim())) {
                    continue;
                }

                INodeDirectory dir = findDirectory(parent, splitPath);
                if (ObjectUtil.isNull(dir)) {
                    INodeDirectory child = new INodeDirectory(splitPath);
                    parent.addChild(child);
                    parent = child;
                } else {
                    parent = dir;
                }
            }
        } finally {
            writeUnlock();
        }
    }

    /**
     * 对目标文件目录树递归查找目录
     */
    private INodeDirectory findDirectory(INodeDirectory dir, String path) {
        if (dir.getChildren().size() == 0) {
            return null;
        }

        INodeDirectory resultDir;
        for (INodeDirectory childDir : dir.getChildren()) {
            if (childDir.getPath().equals(path)) {
                return childDir;
            }

            resultDir = findDirectory(childDir, path);
            if (ObjectUtil.isNotNull(resultDir)) {
                return resultDir;
            }
        }
        return null;
    }

    public boolean create(String filename) {
        try {
            writeLock();
            // 将文件名和路径截取出来
            String[] splitPaths = filename.split(DIRECTORY_DELIMITER);
            String realFilename = splitPaths[splitPaths.length - 1];

            INodeDirectory parent = dirTree;
            for (int i = 0; i < splitPaths.length - 1; i++) {
                if (EMPTY_STR.equals(splitPaths[i].trim())) {
                    continue;
                }
                INodeDirectory dir = findDirectory(parent, splitPaths[i]);
                if (ObjectUtil.isNull(dir)) {
                    INodeDirectory child = new INodeDirectory(splitPaths[i]);
                    parent.addChild(child);
                    parent = child;
                } else {
                    parent = dir;
                }
            }

            // 此时已经过去到文件所在的目录，在此查看目录下是否有该文件
            if (existFile(parent, realFilename)) {
                return true;
            }

            INodeFile file = new INodeFile(realFilename);
            parent.addFile(file);
            return true;
        } finally {
            writeUnlock();
        }
    }

    private boolean existFile(INodeDirectory dir, String realFilename) {
        if (StrUtil.isEmpty(realFilename)) {
            return false;
        }

        if (CollUtil.isNotEmpty(dir.getFiles())) {
            for (INodeFile file : dir.getFiles()) {
                if (realFilename.equals(file.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 代表文件目录树中的一个目录
     */
    interface INode {
    }

    /**
     * 代表文件目录树中的一个目录
     */
    public static class INodeDirectory implements INode {
        private String path;
        private List<INodeDirectory> children;
        private List<INodeFile> files;

        public INodeDirectory(String path) {
            this.path = path;
            this.children = new LinkedList<>();
            this.files = new LinkedList<>();
        }

        public String getPath() {
            return this.path;
        }

        public List<INodeDirectory> getChildren() {
            return this.children;
        }

        public List<INodeFile> getFiles() {
            return files;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public void setChildren(List<INodeDirectory> children) {
            this.children = children;
        }

        public void setFiles(List<INodeFile> files) {
            this.files = files;
        }

        public void addChild(INodeDirectory iNode) {
            this.children.add(iNode);
        }

        public void addFile(INodeFile file) {
            this.files.add(file);
        }

    }

    /**
     * 代表文件目录树中的一个文件
     */
    public static class INodeFile implements INode {

        private String name;

        public INodeFile(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
