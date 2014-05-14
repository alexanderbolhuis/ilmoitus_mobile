//
//  SecondViewController.m
//  Ilmoitus
//
//  Created by Alexander Bolhuis on 22-04-14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import "NewDeclarationViewController.h"
#import "Declaration.h"

@interface NewDeclarationViewController ()
@property (weak, nonatomic) IBOutlet UIPickerView *supervisorList;
@property (weak, nonatomic) IBOutlet UITextField *supervisor;
@end

@implementation NewDeclarationViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
    // Create new blank Declaration
    Declaration *newDeclaration = [[Declaration alloc] init];

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
