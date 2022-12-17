package de.heil_privat.keepasstool;

import org.linguafranca.pwdb.Database;
import org.linguafranca.pwdb.Entry;
import org.linguafranca.pwdb.Group;
import org.linguafranca.pwdb.Visitor;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Stack;

public class DatabaseTreeView extends JPanel {
    private final ApplicationModel model = ApplicationModel.getInstance();
    private final JTree tree;

    public DatabaseTreeView() {
        setMinimumSize(new Dimension(200, 100));
        setLayout(new BorderLayout());
        model.addDatabaseOpenListener(this::setDatabase);
        tree = new JTree(new DefaultMutableTreeNode());
        add(new JScrollPane(tree), BorderLayout.CENTER);
        tree.addTreeSelectionListener(this::onSelection);
    }

    private void onSelection(TreeSelectionEvent e) {
        if (!(e.getSource() instanceof JTree)) {
            model.setSelectedEntry(null);
            return;
        }

        Object lastSelectedPathComponent = ((JTree) e.getSource()).getLastSelectedPathComponent();
        if (!(lastSelectedPathComponent instanceof DefaultMutableTreeNode)) {
            model.setSelectedEntry(null);
            return;
        }

        Object userObject = ((DefaultMutableTreeNode) lastSelectedPathComponent).getUserObject();
        if (!(userObject instanceof EntryWrapper)) {
            model.setSelectedEntry(null);
        } else {
            model.setSelectedEntry(((EntryWrapper) userObject).entry);
        }
    }

    public void setDatabase(Database<?, ?, ?, ?> database) {
        System.out.println("Create TreeModel for " + database);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        root.setUserObject(database);
        Stack<DefaultMutableTreeNode> parents = new Stack<>();
        parents.push(root);
        database.visit(new Visitor() {

            @Override
            public void startVisit(Group group) {
                DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(new GroupWrapper(group));
                parents.peek().add(groupNode);
                parents.push(groupNode);
            }

            @Override
            public void endVisit(Group group) {
                parents.pop();
            }

            @Override
            public void visit(Entry entry) {
                DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(new EntryWrapper(entry));
                treeNode.setAllowsChildren(false);
                parents.peek().add(treeNode);
            }

            @Override
            public boolean isEntriesFirst() {
                return false;
            }
        });
        tree.setRootVisible(false);
        tree.setModel(new DefaultTreeModel(root));
        tree.expandRow(0);
    }

    private static class GroupWrapper {
        final Group<?, ?, ?, ?> group;

        private GroupWrapper(Group<?, ?, ?, ?> group) {
            this.group = group;
        }

        @Override
        public String toString() {
            return group.getName();
        }
    }

    private static class EntryWrapper {
        final Entry<?, ?, ?, ?> entry;

        private EntryWrapper(Entry<?, ?, ?, ?> entry) {
            this.entry = entry;
        }

        @Override
        public String toString() {
            return entry.getTitle();
        }
    }
}
