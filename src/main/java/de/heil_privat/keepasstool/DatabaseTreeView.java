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
import java.util.LinkedList;
import java.util.Stack;
import java.util.function.Consumer;

public class DatabaseTreeView extends JPanel {
    private final ApplicationModel model = ApplicationModel.getInstance();
    private final JTree tree;
    private final java.util.List<Consumer<Group<?, ?, ?, ?>>> groupSelectionListeners = new LinkedList<>();

    public DatabaseTreeView() {
        setMinimumSize(new Dimension(200, 100));
        setLayout(new BorderLayout());
        model.addDatabaseOpenListener(this::setDatabase);
        tree = new JTree(new DefaultMutableTreeNode());
        add(new JScrollPane(tree), BorderLayout.CENTER);
        tree.addTreeSelectionListener(this::onSelection);
    }

    public void addGroupSelectionListener(Consumer<Group<?, ?, ?, ?>> groupSelectionListener) {
        groupSelectionListeners.add(groupSelectionListener);
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
        if (userObject instanceof GroupWrapper) {
            groupSelectionListeners.forEach(l -> l.accept(((GroupWrapper) userObject).group));
        }
    }

    public void setDatabase(Database<?, ?, ?, ?> database) {
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
                //we don't display entries in tree view
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
}
