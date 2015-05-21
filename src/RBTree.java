public class RBTree {
    boolean debug = false;

    enum Color {
        R, B
    }

    Node nil = Node.nil;

    private static class Node {
        static private Node nil = new Node(0);

        static {
            nil.c = Color.B;
        }

        private Node p;
        private Node left;
        private Node right;
        private Color c = Color.R;
        int val;
        int freq = 1;

        Node(int val) {
            this.val = val;
            p = nil;
            left = nil;
            right = nil;
        }
    }

    private Node root = nil;

    public void insert(int val) {
        Node existingNode = findNode(val);
        if (existingNode != nil) {
            if (existingNode.freq == Integer.MAX_VALUE) {
                throw new RuntimeException("Exceed frequency limit.");
            }
            existingNode.freq++;
            return;
        }
        Node n = new Node(val);
        if (root == nil) {
            root = n;
        } else {
            Node cur = root;
            Node prev = nil;
            while (cur != nil) {
                prev = cur;
                if (val < prev.val) {
                    cur = cur.left;
                } else {
                    cur = cur.right;
                }
            }
            if (val < prev.val) {
                prev.left = n;
            } else {
                prev.right = n;
            }
            n.p = prev;
        }
        fixNodeIncert(n);
        if (debug) {
            printTree();
        }

    }

    private void fixNodeIncert(Node n) {
        while (n.p.c == Color.R) {
            if (n.p == n.p.p.left) {
                Node uncle = n.p.p.right;
                if (uncle.c == Color.R) {
                    uncle.c = Color.B;
                    n.p.c = Color.B;
                    n = n.p.p;
                    n.c = Color.R;
                } else {
                    if (n == n.p.right) {
                        leftRotate(n);
                        n = n.left;
                    }
                    n.p.c = Color.B;
                    n.p.p.c = Color.R;
                    rightRotate(n.p);
                }
            } else {
                Node uncle = n.p.p.left;
                if (uncle.c == Color.R) {
                    n.p.c = Color.B;
                    uncle.c = Color.B;
                    n.p.p.c = Color.R;
                    n = n.p.p;
                } else {
                    if (n == n.p.left) {
                        rightRotate(n);
                        n = n.right;
                    }
                    n.p.c = Color.B;
                    n.p.p.c = Color.R;
                    leftRotate(n.p);

                }
            }
        }
        root.c = Color.B;
    }

    private void leftRotate(Node n) {
        Node p = n.p;
        p.right = n.left;
        n.left.p = p;
        n.left = p;
        n.p = p.p;
        p.p = n;
        if (n.p == nil) {
            root = n;
        } else if (n.p.left == p) {
            n.p.left = n;
        } else {
            n.p.right = n;
        }
    }

    private void rightRotate(Node n) {
        Node p = n.p;
        p.left = n.right;
        p.left.p = p;
        n.right = p;
        n.p = p.p;
        p.p = n;
        if (n.p == nil) {
            root = n;
        } else if (n.p.left == p) {
            n.p.left = n;
        } else {
            n.p.right = n;
        }

    }

    public void remove(int val) {
        Node target = findNode(val);
        Node replacer;
        if (target == nil) {
            System.out.println("No such value.");
            return;
        }
        if (target.freq != 1) {
            target.freq--;
            return;
        }
        Color removedColor = target.c;
        if (target.left == nil) {
            replacer = target.right;
            transPlant(target, target.right);
        } else if (target.right == nil) {
            replacer = target.left;
            transPlant(target, target.left);
        } else {
            Node succ = minNode(target.right);
            removedColor = succ.c;
            replacer = succ.right;
            if (succ.p != target) {
                transPlant(succ, succ.right);
                succ.right = target.right;
                succ.right.p = succ;
            } else {
                succ.right.p = succ;
            }
            transPlant(target, succ);
            succ.left = target.left;
            succ.left.p = succ;
            succ.c = target.c;
        }
        if (removedColor == Color.B) {
            fixNodeRemove(replacer);
        }
        if (debug) {
            printTree();
        }

    }

    private Node findNode(int val) {
        Node cur = root;
        while (cur != nil && cur.val != val) {
            if (cur.val > val) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        return cur;
    }

    private void transPlant(Node old, Node cur) {
        if (old.p == nil) {
            root = cur;
        } else if (old.p.left == old) {
            old.p.left = cur;
        } else {
            old.p.right = cur;
        }
        cur.p = old.p;
    }

    private Node minNode(Node cur) {
        if (cur == nil) {
            throw new NullPointerException("Null input to minNode, which is illegal.");
        }
        while (cur.left != nil) {
            cur = cur.left;
        }
        return cur;
    }

    private void fixNodeRemove(Node rt) {
        //Rt is the root of the subtree that has one black height short.
        while (rt != root && rt.c == Color.B) {
            if (rt == rt.p.left) {//If the rt if left child.
                Node sib = rt.p.right;
                if (sib.c == Color.R) {
                    sib.p.c = Color.R;
                    sib.c = Color.B;
                    leftRotate(sib);
                    sib = rt.p.right;
                }
                if (sib.left.c == Color.B && sib.right.c == Color.B) {
                    sib.c = Color.R;
                    rt = rt.p;
                } else {
                    if (sib.right.c == Color.B) {
                        sib.left.c = Color.B;
                        sib.c = Color.R;
                        rightRotate(sib.left);
                        sib = rt.p.right;
                    }
                    sib.c = sib.p.c;
                    sib.right.c = Color.B;
                    sib.p.c = Color.B;
                    leftRotate(sib);
                    rt = root;
                }
            } else {
                Node sib = rt.p.left;
                if (sib.c == Color.R) {
                    sib.p.c = Color.R;
                    sib.c = Color.B;
                    rightRotate(sib);
                    sib = rt.p.left;
                }
                if (sib.left.c == Color.B && sib.right.c == Color.B) {
                    sib.c = Color.R;
                    rt = rt.p;
                } else {
                    if (sib.left.c == Color.B) {
                        sib.right.c = Color.B;
                        sib.c = Color.R;
                        leftRotate(sib.right);
                        sib = rt.p.left;
                    }
                    sib.c = sib.p.c;
                    sib.p.c = Color.B;
                    sib.left.c = Color.B;
                    rightRotate(sib);
                    rt = root;

                }
            }
        }
        rt.c = Color.B;
    }

    public void printTree() {
        if (debug) {
            System.out.println("Color of nil is " + nil.c);
        }
        if (root == nil) {
            System.out.println("Empty tree");
            return;
        }
        List<Node> prev;
        List<Node> cur = new ArrayList<>();
        cur.add(root);
        while (!cur.isEmpty()) {
            prev = cur;
            cur = new ArrayList<>();
            StringBuilder line = new StringBuilder();
            for (Node node : prev) {
                line.append(' ');
                if (node == nil) {
                    line.append('#');
                } else if (node.c == Color.B) {
                    line.append('(');
                    line.append(node.val);
                    line.append(')');
                } else {
                    line.append(node.val);
                }
                if (node != nil) {
                    cur.add(node.left);
                    cur.add(node.right);
                }
            }
            if (debug) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {

                }
            }
            System.out.println(line.toString());

        }
    }

    public boolean contains(int val) {
        return findNode(val) != nil;
    }
    public void debugMode(boolean debug){
        this.debug = debug;
    }

}