package com.ft.simplex.realize.simplex;

import java.util.Scanner;

public class Simplex {
    private double[][] simplexTable;
    private int variables;
    private int restrictions;
    private double[][] solution;
    boolean flag = false;
    private int [] listOfX;

    //данные вводятся вручную
    public Simplex() {
        Scanner in = new Scanner(System.in);

        System.out.print("Input number of variable >> ");
        variables = 1 + in.nextInt();

        System.out.print("Input number of restrictions >> ");
        restrictions = 1 + in.nextInt();

        //инициализация изначального массива и симплекс-таблицы
        simplexTable = new double[restrictions][variables];
        solution = new double[restrictions][variables+restrictions-1];

        //генерация списка индексов х
        listOfX = new int[restrictions-1];
        System.out.println(listOfX.length);
        for (int i = 0; i < listOfX.length; i++)
            listOfX[i]=i+variables-1;

        //заполнение с клавиатуры
        fill();

        formationBasic();
        showSimplex();
        while (check()){
            solve();
            showSimplex();
        }
        System.out.println(getResult());
    }

    //получает данные из вне
    public Simplex(double [][] simplex) {
        this.variables = simplex[0].length;
        this.restrictions = simplex.length;

        //генерация списка индексов х
        listOfX = new int[restrictions-1];
        System.out.println(listOfX.length);
        for (int i = 0; i < listOfX.length; i++)
            listOfX[i]=i+variables-1;

        //инициализация изначального массива и симплекс-таблицы
        simplexTable = new double[restrictions][variables];
        solution = new double[restrictions][variables+restrictions-1];
        for (int i = 0; i < restrictions; i++)
            for(int j = 0; j < variables; j++)
                simplexTable[i][j] = simplex[i][j];

        formationBasic();
        while (check()) {
            solve();
            //вывод промежуточных данных, если нужно
        }
        showSimplex();
        System.out.println(getResult());
    }

    //возвращает корни и значение целевой функции
    public String getResult() {
        String result = "";
        double Fx = 0;
        double [] x = new double[variables-1];
        for (int i = 0; i < listOfX.length;i++)
            if(listOfX[i] < variables) {
                result += "x" + (i+1) + "=" + Math.round(solution[i][solution[i].length-1]) + "  ";
                x[listOfX[i]] = solution[i][solution[i].length-1];
            }
        for (int i = 0 ; i < variables-1; i++)
            Fx += simplexTable[simplexTable.length-1][i]*x[i];
        result += "   " + '\n' + "F(x) =" + Math.round(Fx);
        return result;
    }

    //формирует первый базис
    private void formationBasic() {
        // копирование коефициентов базовых переменных и функции
        for (int i = 0; i < simplexTable.length; i++)
            for (int j = 0; j < simplexTable[i].length - 1; j++)
                solution[i][j] = simplexTable[i][j];

        //заполнение 1 новых х
        for (int i = 0; i < solution.length - 1; i++)
            for (int j = variables - 1; j < solution[0].length; j++)
                if (i + variables - 1 == j)
                    solution[i][j] = 1;

        //копирование свободных членов в последний столбец
        for (int i = 0; i < simplexTable.length; i++)
            solution[i][variables + restrictions - 2] = simplexTable[i][variables - 1];

        //проверка наличия отрицательный свободных членов
        for (int i = 0; i < solution.length - 1; i++) {
            if (solution[i][solution[i].length - 1] < 0) {
                System.out.println(solution[i][solution[i].length - 1] < 0);
                listOfX[i] -= variables-1;
                JordanoGauss(i, i);
                i = -1;
                flag = true;
                //можно вставить промежуточный вывод, если нужно
            }
        }

        //умножение коефициентов функции на -1
        for (int i = 0; i < solution[solution.length - 1].length; i++)
            solution[solution.length - 1][i] = -1 * solution[solution.length - 1][i];

        solution[solution.length - 1][solution[0].length - 1] = 0.0;
    }

    //заполнение изначальной матрицы
    private void fill () {
        Scanner in = new Scanner(System.in);
        for (int i = 0 ; i < simplexTable.length; i++) {
            if ( i == restrictions-1)
                System.out.print("\nF(x): \n");
            else
                System.out.print("\nRestriction " + (i+1) + ":\n");
            for (int j = 0; j < variables; j++) {
                if (j == variables-1) {
                    if (i != restrictions-1) {
                        System.out.print("Input absolute term >> ");
                        simplexTable[i][j] = in.nextDouble();
                    } else simplexTable[i][j] = 0;
                }
                else {
                    System.out.print("Input x" + (j+1) + " >> ");
                    simplexTable[i][j] = in.nextDouble();
                }
            }
        }
    }

    //вывод изначальной матрицы
    private void showBasicMatrix () {
        for (int i = 0 ; i < simplexTable.length; i++) {
            System.out.print('\n');
            if( i == restrictions-1)
                System.out.print("F(x):");
            for (int j = 0; j < variables; j++) {
                if ( j == variables-1) {
                    System.out.print(" =");
                    if ( i != restrictions-1)
                       System.out.printf("% 10.2f", simplexTable[i][j]);
                }
                else {
                    if (j > 0)
                        System.out.print(" +");
                    System.out.printf("% 10.2f", simplexTable[i][j]);
                    System.out.print("x" + (j + 1));
                }
            }
        }

    }

    //вывод симплекс таблицы
    private void showSimplex () {
        for (int i = 0 ; i < solution.length; i++) {
            System.out.print('\n');
            if( i == solution.length-1)
                System.out.print("F(x):");
            for (int j = 0; j < solution[0].length; j++) {
                if ( j == solution[0].length-1) {
                    System.out.print(" =");
                    System.out.printf("% 10.2f", solution[i][j]);
                }
                else {
                    if (j > 0)
                        System.out.print(" +");
                    System.out.printf("% 10.2f", solution[i][j]);
                    System.out.print("x" + (j + 1));
                }
            }
        }
    }
    //проверка оптимальности симплекс таблицы
    private boolean check() {
        for (int i = 0; i < solution[solution.length-1].length;i++)
            if (solution[solution.length-1][i] < 0) return true;
        return false;
    }

    //решение симплекс метода
    private void solve () {
        int jmin;
        if(flag) {
            jmin = getMaxAbs(solution[solution.length - 1]);
            flag = false;
        }
        else jmin = Matrix.getMin(solution[solution.length - 1]); // индекс разрешающего столбца
        System.out.println(jmin);
        double[] colSimplexReletivity = new double[solution.length];
        for (int i = 0; i < colSimplexReletivity.length; i++) {
            if (solution[i][jmin] < 0) colSimplexReletivity[i] = 99999999;
            else colSimplexReletivity[i] = (solution[i][variables + restrictions - 2] / solution[i][jmin]); // последний элемент делим на разрешающий столбец
        }
        int imin = Matrix.getMin(colSimplexReletivity); // индекс разрешающей строки
        System.out.println(imin + "  " + jmin);
        JordanoGauss(imin, jmin);
        for (int i = 0; i < listOfX.length; i++)
            if (listOfX[i] == imin)
                listOfX[i] = jmin;
    }

    //находит наибольшее по модулю, если были отрицательные свободные члены
    private int getMaxAbs(double [] row) {
        double max = -9999999;
        int maxi = -1;
        for (int j = 0; j < row.length; j++) {
            if (Math.abs(row[j]) > max) {
                maxi = j;
                max = Math.abs(row[j]);
            }
        }
        return maxi;
    }

    //проводит пересчет элементов методом Жордано-Гаусса
    private  void JordanoGauss(int imin, int jmin) {
        double [][] temp = new double[solution.length][solution[0].length];
        for (int i = 0; i  < solution.length; i ++ )
            for (int j = 0; j < solution[i].length; j++) {
                if (i != imin)
                    temp[i][j] = solution[i][j] - ((solution[imin][j] * solution[i][jmin]) / solution[imin][jmin]);
            }
        for (int i = 0; i < solution[imin].length; i ++) {
            if ( i != jmin)                                                        //все элементы разрешающей строки, кроме того,
                temp[imin][i] = solution[imin][i] / solution[imin][jmin];      //что на пересечении, делим на этот элемент на пересечении.
        }
        temp[imin][jmin] = 1.0; // разрешающий элемент = 1
        Matrix.matrixCopy(solution,temp);
    }


}
